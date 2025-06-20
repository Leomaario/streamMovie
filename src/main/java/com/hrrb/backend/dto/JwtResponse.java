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
    }
}
