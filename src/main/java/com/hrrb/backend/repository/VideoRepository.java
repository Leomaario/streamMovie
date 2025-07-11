package com.hrrb.backend.repository;

import com.hrrb.backend.model.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VideoRepository extends JpaRepository<Video, Long> {

    Optional<Video> findTopByOrderByIdDesc();
    List<Video> findByCatalogoId(Long catalogoId);
}
