package com.hrrb.backend.security.jwt;

import com.hrrb.backend.security.services.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// Este é o nosso "guarda"
public class AuthTokenFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);

    // Este é o método principal que faz a verificação
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            // 1. Tenta extrair o token da requisição
            String jwt = parseJwt(request);

            // 2. Se o token existir e for válido...
            if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
                // 3. ...extrai o nome de usuário do token
                String username = jwtUtils.getUserNameFromJwtToken(jwt);

                // 4. ...carrega os detalhes do usuário do banco de dados
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                // 5. ...cria um objeto de autenticação
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null, // Não precisamos das credenciais (senha), pois já validamos pelo token
                                userDetails.getAuthorities());

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // 6. ...e finalmente, "autentica" o usuário no contexto de segurança do Spring
                // A partir daqui, o Spring sabe que o usuário está logado para esta requisição
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            logger.error("Não foi possível autenticar o usuário: {}", e.getMessage());
        }

        // 7. Passa a requisição para o próximo filtro na cadeia
        filterChain.doFilter(request, response);
    }

    // Método auxiliar para extrair o token do cabeçalho "Authorization"
    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");

        // Verifica se o cabeçalho existe e começa com "Bearer "
        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            // Retorna apenas a string do token (remove o "Bearer ")
            return headerAuth.substring(7);
        }

        return null;
    }
}