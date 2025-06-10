
package com.hrrb.backend.config; // Adapte o nome do seu pacote

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Aplica a configuração de CORS que a gente define abaixo
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                // Desabilita o CSRF (uma forma mais direta e recomendada)
                .csrf(AbstractHttpConfigurer::disable)
                // A REGRA MAIS IMPORTANTE PARA O TESTE:
                // Autoriza TODAS as requisições, sem nenhuma exceção.
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()
                );
        return http.build();
    }

    // A gente mantém nossa configuração de CORS, mas deixamos ela BEM aberta para o teste.
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Para o teste, vamos ser o mais permissivo possível, aceitando qualquer origem.
        configuration.setAllowedOrigins(List.of("*"));

        // Permite TODOS os métodos HTTP comuns.
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD", "PATCH"));

        // Permite TODOS os cabeçalhos (headers).
        configuration.setAllowedHeaders(List.of("*"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // Aplica a configuração acima para TODAS as rotas da nossa API.
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
