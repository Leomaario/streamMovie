package com.hrrb.backend.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data //FAZ O LOMBOK CRIAR OS GETTERS E SETTERS
@Entity //AVISA PRO JAVA QUE ESSE CLASSE REPRESENTA UMA TABELA NO BANCO DE DADOS
@Table(name="catalogos") // DIZ QUAL É O NOME QUE ESSA CLASSE REPRESENTA NO BANCO
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) // <<<--- ADICIONE ESTA LINHA AQUI
public class Catalogo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome", nullable = false, unique = true)
    private String nome;

    @Column(name = "descricao", columnDefinition = "TEXT")
    private String descricao;

    @Column(name = "icone")
    private String icone;

    @Column(name = "tag")
    private String tag;

    @Column(name = "posicao_menu")
    private Integer posicaoMenu;

    @CreationTimestamp
    @Column(name = "data_criacao", nullable = false, updatable = false)
    private LocalDateTime dataCriacao;

    @UpdateTimestamp
    @Column(name = "data_atualizacao", nullable = false)
    private LocalDateTime dataAtualizacao;
}
