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
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private AuthEntryPointJwt unauthorizedHandler;

    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // CORREÇÃO: A lista de origens deve conter apenas os endereços do SEU FRONTEND.
        configuration.setAllowedOrigins(List.of("https://souzalink-coach.onrender.com", "http://localhost:5173"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Requested-With", "Accept"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth ->
                        // CORREÇÃO: A ordem das regras foi ajustada. As mais específicas vêm primeiro.
                        auth
                                // 1. Rotas públicas
                                .requestMatchers("/api/auth/**").permitAll()

                                // 2. Rotas de ADMIN
                                .requestMatchers("/api/usuarios/**").hasRole("ADMIN")
                                .requestMatchers("/api/dashboard/**").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.GET, "/api/grupos").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.POST, "/api/catalogos/**").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.PUT, "/api/catalogos/**").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.DELETE, "/api/catalogos/**").hasRole("ADMIN")

                                // 3. Rotas de LÍDER e ADMIN
                                .requestMatchers(HttpMethod.POST, "/api/videos/**").hasAnyRole("ADMIN", "LIDER")
                                .requestMatchers(HttpMethod.PUT, "/api/videos/**").hasAnyRole("ADMIN", "LIDER")
                                .requestMatchers(HttpMethod.DELETE, "/api/videos/**").hasAnyRole("ADMIN", "LIDER")

                                // 4. Rotas de qualquer usuário autenticado
                                .requestMatchers(HttpMethod.GET, "/api/videos/**", "/api/catalogos/**", "/api/certificados/**", "/api/progresso/**", "/api/catalogos-detalhes/**", "/api/profile/**").authenticated()

                                // 5. A REGRA GERAL "anyRequest" POR ÚLTIMO
                                .anyRequest().authenticated()
                );

        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}