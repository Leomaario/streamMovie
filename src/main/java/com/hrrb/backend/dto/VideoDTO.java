package com.hrrb.backend.dto;

import com.hrrb.backend.model.Video;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class VideoDTO {

    //Informações que o front precisa
    private Long id;
    private String titulo;
    private String descricao;
    private Integer duracaoSegundos;
    private Long catalogoId;
    private String catalogoNome;

    // Construtor que transforma um objeto Video (da entidade) em um VideoDTO
    public VideoDTO(Video video){
        this.id = video.getId();
        this.titulo = video.getTitulo();
        this.descricao = video.getDescricao();
        this.duracaoSegundos = video.getDuracaoSegundos();
        if (video.getCatalogo() != null){
            this.catalogoId = video.getCatalogo().getId();
            this.catalogoNome = video.getCatalogo().getNome();
        }
    }
}
