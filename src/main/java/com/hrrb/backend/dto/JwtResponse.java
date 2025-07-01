package com.hrrb.backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List; // Adicione este import

@NoArgsConstructor
@Data
public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private Long id;
    private String usuario;
    private String email;
    private List<String> roles; // <<< O campo que faltava

    // O construtor agora aceita a lista de permissões
    public JwtResponse(String jwt, Long id, String username, String email, List<String> roles) {
        this.token = jwt;
        this.id = id;
        this.usuario = username;
        this.email = email;
        this.roles = roles;
    }
}