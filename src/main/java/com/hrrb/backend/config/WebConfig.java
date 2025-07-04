package com.hrrb.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    // --- ADICIONE ESTE CONSTRUTOR ---
    public WebConfig() {
        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        System.out.println("!!! O WebConfig FOI CARREGADO PELO SPRING !!!");
        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
    }
    // ------------------------------------

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        System.out.println(">>> Configurando MessageConverters...");
        converters.add(new MappingJackson2HttpMessageConverter());
        converters.add(new ResourceHttpMessageConverter());
        System.out.println(">>> Conversores de JSON e Resource ADICIONADOS.");
    }
}