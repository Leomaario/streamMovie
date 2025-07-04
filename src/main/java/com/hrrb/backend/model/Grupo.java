package com.hrrb.backend.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Data
@Entity
@Table(name = "grupos")
public class Grupo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nome;

    @Column(nullable = true)
    private String descricao;

    @OneToMany(mappedBy = "grupo")
    @JsonManagedReference("grupo-usuarios") // Adicionando referência para a lista de usuários
    private List<Usuario> usuarios;

}