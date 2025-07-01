package com.hrrb.backend.repository;

import com.hrrb.backend.model.ProgressoUsuarioVideo;
import com.hrrb.backend.model.Usuario;
import com.hrrb.backend.model.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProgressoUsuarioVideoRepository extends JpaRepository<ProgressoUsuarioVideo, Long> {

    /**
     * Encontra um registo de progresso específico para um utilizador e um vídeo.
     * Isto vai nos permitir verificar se o utilizador já começou a assistir a este vídeo antes.
     * @param usuario O objeto do utilizador.
     * @param video O objeto do vídeo.
     * @return Um Optional contendo o progresso, se existir.
     */
    Optional<ProgressoUsuarioVideo> findByUsuarioAndVideo(Usuario usuario, Video video);

}