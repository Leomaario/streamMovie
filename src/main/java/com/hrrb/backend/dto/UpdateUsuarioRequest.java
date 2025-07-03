package com.hrrb.backend.dto;

import lombok.Data;

// ...
@Data
public class UpdateUsuarioRequest {
    private String nome;
    private String email;
    private String grupo;
    private String permissoes;
    private String senha; // Opcional, para reset de senha
}