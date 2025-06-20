package com.hrrb.backend.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String usuario;
    private String senha;
}