package com.hrrb.backend.dto;

import lombok.Data;

@Data
public class UpdateVideoRequest {
    private String titulo;
    private String descricao;
    private Integer duracaoSegundos;
    private String urlDoVideo;
    private Long catalogoId;
}
