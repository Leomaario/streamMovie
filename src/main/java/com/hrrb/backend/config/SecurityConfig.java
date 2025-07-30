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

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final AuthEntryPointJwt unauthorizedHandler;

    @Autowired
    public SecurityConfig(AuthEntryPointJwt unauthorizedHandler) {
        this.unauthorizedHandler = unauthorizedHandler;
    }

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
        configuration.setAllowedOrigins(List.of("https://souzalink-coach.onrender.com", "http://localhost:5173"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD", "PATCH"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

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
                        auth
                                // =================================================================
                                // BLOCO 1: ROTAS PÚBLICAS (não precisam de login)
                                // =================================================================
                                .requestMatchers("/api/auth/login").permitAll()
                                .requestMatchers("/api/auth/health").permitAll()
                                .requestMatchers("/api/catalogos/keep-alive").permitAll()

                                // =================================================================
                                // BLOCO 2: ROTAS DE ADMIN (precisa ter a permissão "ADMIN")
                                // Admins podem fazer TUDO (GET, POST, PUT, DELETE) nessas rotas.
                                // =================================================================
                                .requestMatchers("/api/auth/registrar").hasAuthority("ADMIN")
                                .requestMatchers("/api/usuarios/**").hasAuthority("ADMIN")
                                .requestMatchers("/api/dashboard/**").hasAuthority("ADMIN")
                                .requestMatchers("/api/grupos/**").hasAuthority("ADMIN")
                                .requestMatchers("/api/catalogos").hasAuthority("ADMIN") // POST, PUT, DELETE
                                .requestMatchers("/api/catalogos/**").hasAuthority("ADMIN")
                                .requestMatchers("/api/videos/**").hasAuthority("ADMIN")
                                .requestMatchers("/api/dashboard/stats").hasAuthority("ADMIN")
                                .requestMatchers("/api/progresso/**").hasAuthority("ADMIN")
                                .requestMatchers("/api/relatorios/**").hasAuthority("ADMIN")




                                // =================================================================
                                // BLOCO 3: ROTAS DE GERENCIAMENTO DE VÍDEOS (LIDER & ADMIN)
                                // Apenas LIDER e ADMIN podem criar, editar ou deletar vídeos.
                                // =================================================================
                                .requestMatchers(HttpMethod.POST, "/api/videos").hasAnyAuthority("ADMIN", "LIDER")
                                .requestMatchers(HttpMethod.PUT, "/api/videos/**").hasAnyAuthority("ADMIN", "LIDER")
                                .requestMatchers(HttpMethod.DELETE, "/api/videos/**").hasAnyAuthority("ADMIN", "LIDER")
                                .requestMatchers(HttpMethod.DELETE, "/api/videos").hasAnyAuthority("ADMIN", "LIDER")


                                // =================================================================
                                // BLOCO 4: ROTAS DE VISUALIZAÇÃO (qualquer usuário logado)
                                // Qualquer usuário logado (USER, LIDER, ADMIN) pode VISUALIZAR (GET) os vídeos.
                                // =================================================================
                                .requestMatchers(HttpMethod.GET, "/api/videos", "/api/videos/buscar/**").authenticated()
                                .requestMatchers("/api/progresso/**").authenticated() // Ver e marcar progresso


                                //========================================================================
                                // BLOCO 5: PAINEL DE ADM, TODOS OS METHOD

                                //GET
                                .requestMatchers(HttpMethod.GET, "/api/dashboard/").hasAnyAuthority("ADMIN", "LIDER")
                                .requestMatchers(HttpMethod.GET, "/api/dashboard/stats").hasAnyAuthority("ADMIN", "LIDER")
                                .requestMatchers(HttpMethod.GET, "/api/usuarios").hasAnyAuthority("ADMIN", "LIDER")
                                .requestMatchers(HttpMethod.GET, "/api/user-dashboard/data").hasAnyAuthority("ADMIN", "LIDER")
                                .requestMatchers(HttpMethod.GET, "/api/grupos").hasAnyAuthority("ADMIN", "LIDER")
                                //PUT
                                .requestMatchers(HttpMethod.PUT, "/api/dashboard/").hasAnyAuthority("ADMIN", "LIDER")
                                .requestMatchers(HttpMethod.PUT, "/api/dashboard/stats").hasAnyAuthority("ADMIN", "LIDER")
                                .requestMatchers(HttpMethod.PUT, "/api/grupos/**").hasAnyAuthority("ADMIN", "LIDER")
                                .requestMatchers(HttpMethod.PUT, "/api/videos/**").hasAnyAuthority("ADMIN", "LIDER")
                                .requestMatchers(HttpMethod.PUT, "/api/progresso/**").hasAnyAuthority("ADMIN", "LIDER")
                                //POST
                                .requestMatchers(HttpMethod.POST, "/api/grupos").hasAnyAuthority("ADMIN", "LIDER")
                                .requestMatchers(HttpMethod.POST, "/api/usuarios").hasAnyAuthority("ADMIN", "LIDER")
                                .requestMatchers(HttpMethod.POST, "/api/videos").hasAnyAuthority("ADMIN", "LIDER")
                                //DELETE
                                .requestMatchers(HttpMethod.DELETE, "/api/grupos/**").hasAnyAuthority("ADMIN", "LIDER")
                                .requestMatchers(HttpMethod.DELETE, "/api/usuarios/**").hasAnyAuthority("ADMIN", "LIDER")
                                .requestMatchers(HttpMethod.DELETE, "/api/videos/**").hasAnyAuthority("ADMIN", "LIDER")
                                .requestMatchers(HttpMethod.DELETE, "/api/progresso/**").hasAnyAuthority("ADMIN", "LIDER")
                                .requestMatchers(HttpMethod.DELETE, "/api/**").hasAnyAuthority("ADMIN", "LIDER")

                                // =================================================================
                                // BLOCO 5: REGRA FINAL (segurança extra)
                                // Qualquer outra requisição não listada acima precisa de login.
                                // =================================================================
                                .anyRequest().authenticated()
                );
        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
