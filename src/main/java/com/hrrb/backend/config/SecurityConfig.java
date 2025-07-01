package com.hrrb.backend.config;

import com.hrrb.backend.security.jwt.AuthEntryPointJwt;
import com.hrrb.backend.security.jwt.AuthTokenFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    // Injeta o nosso tratador de erros de autenticação
    @Autowired
    private AuthEntryPointJwt unauthorizedHandler;

    // Injeta o nosso filtro JWT (que já é um @Component)
    @Autowired
    private AuthTokenFilter authTokenFilter;

    // O método @Bean que criava o filtro com 'new' foi REMOVIDO.

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(withDefaults())
                .csrf(csrf -> csrf.disable())
                .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .securityContext(context -> context.securityContextRepository(new RequestAttributeSecurityContextRepository()))
                .authorizeHttpRequests(authorize -> authorize
                        // Rotas públicas (login, etc)
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/api/auth/**").permitAll()

                        // Rotas de ADMIN (ex: gerir usuários)
                        .requestMatchers("/api/usuarios/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/grupos").hasRole("ADMIN")

                        // Rotas de LÍDER e ADMIN (gerir vídeos e catálogos)
                        .requestMatchers(HttpMethod.POST, "/api/videos/**").hasAnyRole("ADMIN", "LIDER")
                        .requestMatchers(HttpMethod.PUT, "/api/videos/**").hasAnyRole("ADMIN", "LIDER")
                        .requestMatchers(HttpMethod.DELETE, "/api/videos/**").hasAnyRole("ADMIN", "LIDER")
                        .requestMatchers(HttpMethod.POST, "/api/catalogos/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/catalogos/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/catalogos/**").hasRole("ADMIN")

                        // Rotas que QUALQUER usuário logado pode acessar (ver conteúdo)
                        .requestMatchers(HttpMethod.GET, "/api/videos/**", "/api/catalogos/**", "/api/certificados/**").authenticated()

                        // Qualquer outra requisição que sobrar, precisa estar autenticado
                        .anyRequest().authenticated()
                );

        http.addFilterBefore(authTokenFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:5173"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Requested-With"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}