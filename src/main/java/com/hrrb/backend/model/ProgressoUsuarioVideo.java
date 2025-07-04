package com.hrrb.backend.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "progresso_usuario_videos")
public class ProgressoUsuarioVideo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relação: Muitos progressos pertencem a UM Utilizador
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    @JsonBackReference("usuario-progressos") // <<< ANOTAÇÃO ADICIONADA
    private Usuario usuario;

    // Relação: Muitos progressos (de diferentes utilizadores) podem ser sobre UM Vídeo
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "video_id", nullable = false)
    @JsonBackReference("video-progressos")
    private Video video;

    @Column(nullable = false)
    private boolean concluido = false;

    @Column(name = "data_conclusao")
    private LocalDateTime dataConclusao;

    @CreationTimestamp
    @Column(name = "data_criacao", nullable = false, updatable = false)
    private LocalDateTime dataCriacao;

    @UpdateTimestamp
    @Column(name = "data_atualizacao", nullable = false)
    private LocalDateTime dataAtualizacao;
}