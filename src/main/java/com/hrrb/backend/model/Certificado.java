package com.hrrb.backend.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "certificados")
public class Certificado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "titulo_curso", nullable = false)
    private String tituloCurso;

    @Column(name = "data_emissao")
    private LocalDateTime dataEmissao;

    // Usando o nome da sua coluna: 'codigo_verificacao'
    @Column(name = "codigo_verificacao", nullable = false, unique = true)
    private String codigoValidacao;

    // Relação com o Utilizador
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    @JsonIgnore
    private Usuario usuario;

    // Relação com o Catálogo (a sua ótima ideia!)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "catalogo_id")
    @JsonIgnore
    @JsonBackReference("usuario-certificados")
    private Catalogo catalogo;

    // Colunas de data automáticas
    @CreationTimestamp
    @Column(name = "data_criacao", nullable = false, updatable = false)
    private LocalDateTime dataCriacao;

    @UpdateTimestamp
    @Column(name = "data_atualizacao", nullable = false)
    private LocalDateTime dataAtualizacao;
}