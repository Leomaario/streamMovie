package com.hrrb.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "usuarios")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Não precisa de @Column(name="nome") porque o nome do campo já é "nome"
    @Column(nullable = false, unique = true)
    private String nome;

    // Este campo mapeia para a coluna "usuario"
    @Column(nullable = false, unique = true)
    private String usuario;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String senha;

    // Aqui o @Column(name=...) é necessário porque o nome do campo é diferente da coluna
    @Column(name = "foto_perfil_path")
    private String fotoPerfilPath;

    @Column(nullable = false)
    private String grupo;

    // CORREÇÃO 2: Este campo agora mapeia para a coluna "permissoes", não "usuario"
    @Column(name = "permissoes", nullable = false)
    private String permissoes;

    // ADICIONADO: Mapeamento para as colunas de data que criamos no banco
    @CreationTimestamp
    @Column(name = "data_criacao", nullable = false, updatable = false)
    private LocalDateTime dataCriacao;

    @UpdateTimestamp
    @Column(name = "data_atualizacao", nullable = false)
    private LocalDateTime dataAtualizacao;

}
