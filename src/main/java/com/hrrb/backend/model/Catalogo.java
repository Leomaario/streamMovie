package com.hrrb.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Entity
@Table(name = "catalogos")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Catalogo {


    @OneToMany(mappedBy = "catalogo", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Video> videos;

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

    // Se você quiser mapear o lado inverso (ver todos os vídeos de um catálogo)
    // Precisamos adicionar a anotação @JsonIgnore aqui para evitar um loop infinito de serialização
    // @OneToMany(mappedBy = "catalogo", cascade = CascadeType.ALL, orphanRemoval = true)
    // @com.fasterxml.jackson.annotation.JsonIgnore // <<-- Importante para evitar loops
    // private Set<Video> videos = new HashSet<>();
}
