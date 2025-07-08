package com.hrrb.backend.repository;

import com.hrrb.backend.model.ProgressoUsuarioVideo;
import com.hrrb.backend.model.Usuario;
import com.hrrb.backend.model.Video;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProgressoUsuarioVideoRepository extends JpaRepository<ProgressoUsuarioVideo, Long> {


    Optional<ProgressoUsuarioVideo> findByUsuarioAndVideo(Usuario usuario, Video video);
    List<ProgressoUsuarioVideo> findByUsuarioAndConcluido(Usuario usuario, boolean concluido);

    long countByUsuario(Usuario usuario);
    long countByUsuarioAndConcluido(Usuario usuario, boolean concluido);
    List<ProgressoUsuarioVideo> findByUsuario(Usuario usuario);




}