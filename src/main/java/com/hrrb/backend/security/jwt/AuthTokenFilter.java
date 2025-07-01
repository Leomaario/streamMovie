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
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class AuthTokenFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        logger.info("==================== INÍCIO DO FILTRO ====================");
        logger.info("A processar requisição para: {}", request.getRequestURI());

        try {
            String jwt = parseJwt(request);

            if (jwt == null) {
                logger.warn("Token JWT não encontrado no cabeçalho Authorization.");
            } else {
                logger.info("Token JWT encontrado: {}", jwt);

                boolean isTokenValid = jwtUtils.validateJwtToken(jwt);
                logger.info("O token é válido? {}", isTokenValid);

                if (isTokenValid) {
                    String username = jwtUtils.getUserNameFromJwtToken(jwt);
                    logger.info("Nome de utilizador extraído do token: {}", username);

                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                    logger.info("UserDetails carregado para o utilizador: {}", userDetails.getUsername());

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities());

                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    logger.info("!!! Utilizador {} autenticado com sucesso no contexto de segurança. Permissões: {}", username, userDetails.getAuthorities());
                }
            }
        } catch (Exception e) {
            logger.error("!!! EXCEÇÃO no AuthTokenFilter: {}", e.getMessage(), e);
        }

        logger.info("A passar a requisição para o próximo filtro na cadeia...");
        logger.info("==================== FIM DO FILTRO ====================");
        filterChain.doFilter(request, response);
    }

    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");
        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }
        return null;
    }
}