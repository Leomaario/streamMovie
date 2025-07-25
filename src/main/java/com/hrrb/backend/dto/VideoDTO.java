package com.hrrb.backend.dto;

import com.hrrb.backend.model.Video;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class VideoDTO {


    private Long id;
    private String titulo;
    private String descricao;
    private Integer duracaoSegundos;
    private String urlDoVideo;
    private Long catalogoId;
    private String catalogoNome;

    public VideoDTO(Video video){
        this.id = video.getId();
        this.titulo = video.getTitulo();
        this.descricao = video.getDescricao();
        this.duracaoSegundos = video.getDuracaoSegundos();
        this.urlDoVideo = video.getVideoUrl();

        if (video.getCatalogo() != null){
            this.catalogoId = video.getCatalogo().getId();
            this.catalogoNome = video.getCatalogo().getNome();
        }
    }
}