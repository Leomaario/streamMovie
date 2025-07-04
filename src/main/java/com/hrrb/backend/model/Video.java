package com.hrrb.backend.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "videos")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) // <<<--- ADICIONE ESTA LINHA AQUI
public class Video {



    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "titulo", nullable = false)
    private String titulo;

    @Column(name = "descricao", columnDefinition = "TEXT")
    private String descricao;

    @Column(name = "caminho_arquivo", nullable = false, unique = true)
    String caminhoArquivo;

    @Column(name = "duracao_segundos")
    Integer duracaoSegundos;

    @Column(name = "formato")
    private String formato;

    @Column(name = "data_upload")
    private LocalDateTime dataUpload;

    //AGORA OS LINKS COM OUTRAS TABELAS
    // @ManyToOne: Indica a relação (Muitos Vídeos para Um Catálogo).
    //@JoinColumn(name = "catalogo_id"): Diz ao JPA que a coluna catalogo_id na tabela videos é a chave estrangeira que "linka" este vídeo ao seu catálogo pai.
    @ManyToOne(fetch = FetchType.LAZY) //Muitos videos podem pertencer a um catalogo
    @JoinColumn(name = "catalogo_id", nullable = false)
    @JsonBackReference
    private Catalogo catalogo;

    //COLUNAS DE DATA GERENCIADAS PELO HIBERNATE
    @CreationTimestamp
    @Column(name = "data_criacao", nullable = false, updatable = false)
    private LocalDateTime dataCriacao;

    @UpdateTimestamp
    @Column(name = "data_atualizacao", nullable = false)
    private LocalDateTime dataAtualizacao;

}

