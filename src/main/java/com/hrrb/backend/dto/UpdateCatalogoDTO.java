package com.hrrb.backend.dto;

import com.hrrb.backend.model.Catalogo;

import java.time.LocalDateTime;

public class UpdateCatalogoDTO {
    private Long id;
    private String nome;

    public UpdateCatalogoDTO(){

    }

    public UpdateCatalogoDTO(Catalogo catalogo){
        this.id = catalogo.getId();
        this.nome = catalogo.getNome();
    }
}
