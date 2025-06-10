package com.hrrb.backend.repository;

import com.hrrb.backend.model.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VideoRepository extends JpaRepository<Video, Long> {


    List<Video> findByCatalogoId(Long catalogoId);
}
