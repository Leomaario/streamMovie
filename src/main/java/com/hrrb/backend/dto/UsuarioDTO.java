package com.hrrb.backend.dto;

import com.hrrb.backend.model.Usuario;
import lombok.Data;

import java.time.LocalDateTime;


@Data
public class UsuarioDTO {
    private Long id;
    private String nome;
    private String usuario;
    private String email;
    private String grupo;
    private String permissoes;
    private LocalDateTime dataCriacao;
    public UsuarioDTO(){
    }

    public UsuarioDTO(Usuario usuario) {
        this.id = usuario.getId();
        this.nome = usuario.getNome();
        this.usuario = usuario.getUsuario();
        this.email = usuario.getEmail();
        this.permissoes = usuario.getPermissoes();
        this.dataCriacao = usuario.getDataCriacao();
        if (usuario.getGrupo() != null) {
            this.grupo = usuario.getGrupo().getNome();
        } else {
            this.grupo = "Sem Grupo"; // Ou null, se preferir
        }
    }
    }