package com.hrrb.backend.dto;

import lombok.Data;

@Data
public class UpdateVideoRequest {
    private String titulo;
    private String descricao;
    private Long catalogoId;
}
