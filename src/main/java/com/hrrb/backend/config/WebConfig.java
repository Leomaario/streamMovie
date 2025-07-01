package com.hrrb.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

// Esta classe vai arrumar a casa e garantir que todos os "tradutores"
// essenciais estão disponíveis para a sua aplicação.
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        // Adiciona o tradutor para JSON (para os nossos DTOs como MessageResponse)
        converters.add(new MappingJackson2HttpMessageConverter());

        // Adiciona o tradutor para arrays de bytes
        converters.add(new ByteArrayHttpMessageConverter());

        // ADICIONA O TRADUTOR PARA FICHEIROS/RECURSOS (A SOLUÇÃO PARA O STREAM)
        converters.add(new ResourceHttpMessageConverter());
    }
}