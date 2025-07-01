package com.hrrb.backend.dto;
import lombok.Data;

@Data
public class RegistroRequest {
    private String nome;
    private String usuario;
    private String email;
    private String senha;
    private String grupo;
    private String permissoes;
}