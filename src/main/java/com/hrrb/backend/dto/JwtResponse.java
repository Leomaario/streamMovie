package com.hrrb.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private Long id;
    private String usuario;
    private String email;

    public JwtResponse(String jwt, Long id, String username, String email) {
            this.token = jwt; // Atribui o token recebido ao campo 'token'
            this.id = id;
            this.usuario = username; // Atribui o 'username' recebido ao campo 'usuario'
            this.email = email;
    }
}
