package com.hrrb.backend.dto;

import com.hrrb.backend.model.Catalogo;
import lombok.Data;

@Data
public class CatalogoDTO {
    private Long id;
    private String nome;
    private String descricao;
    private String icone;
    private String tag;
    private String caminhoPasta;

    public CatalogoDTO(Catalogo catalogo) {
        this.id = catalogo.getId();
        this.nome = catalogo.getNome();
        this.descricao = catalogo.getDescricao();
        this.icone = catalogo.getIcone();
        this.tag = catalogo.getTag();
        this.caminhoPasta = catalogo.getCaminhoPasta();
    }
}