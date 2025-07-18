package com.hrrb.backend.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

// Esta classe trata erros de autenticação para a API REST
@Component
public class AuthEntryPointJwt implements AuthenticationEntryPoint {
    private static final Logger logger = LoggerFactory.getLogger(AuthEntryPointJwt.class);

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException, ServletException {
        logger.error("Erro de acesso não autorizado: {}", authException.getMessage());

        // Define o tipo de conteúdo da resposta como JSON
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        // Define o status da resposta como 401 (Não Autorizado)
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        // Cria um corpo de resposta JSON estruturado
        final Map<String, Object> body = new HashMap<>();
        body.put("status", HttpServletResponse.SC_UNAUTHORIZED);
        body.put("error", "Não autorizado");
        // A mensagem de erro específica que queremos mostrar no frontend!
        body.put("message", authException.getMessage());
        body.put("path", request.getServletPath());

        // Usa o ObjectMapper do Spring para escrever o JSON na resposta
        final ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(response.getOutputStream(), body);

    }
}