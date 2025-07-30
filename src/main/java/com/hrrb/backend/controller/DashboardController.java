package com.hrrb.backend.controller;

import com.hrrb.backend.dto.DashboardStatsDTO;
import com.hrrb.backend.repository.UsuarioRepository;
import com.hrrb.backend.repository.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping("/stats")
    public ResponseEntity<DashboardStatsDTO> getStats() {
        long totalCursos = videoRepository.count();
        long totalUsuarios = usuarioRepository.count();
        long totalRelatorios = 0;


        DashboardStatsDTO stats = new DashboardStatsDTO(totalCursos, totalUsuarios, totalRelatorios);
        return ResponseEntity.ok(stats);
    }
}