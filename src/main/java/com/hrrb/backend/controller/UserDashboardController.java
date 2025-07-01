package com.hrrb.backend.controller;

import com.hrrb.backend.dto.VideoDTO;
import com.hrrb.backend.model.Video;
import com.hrrb.backend.repository.UsuarioRepository;
import com.hrrb.backend.repository.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/user-dashboard")
@CrossOrigin(origins = "*")
public class UserDashboardController {

    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping("/data")
    public ResponseEntity<?> getDashboardData() {
        // Busca o último vídeo adicionado como "destaque"
        Optional<Video> videoDestaqueOpt = videoRepository.findTopByOrderByIdDesc();
        VideoDTO videoDestaqueDto = videoDestaqueOpt.map(VideoDTO::new).orElse(null);

        // Busca o total de cursos no sistema
        long totalCursos = videoRepository.count();

        // Monta a resposta com um Map
        Map<String, Object> responseData = Map.of(
                "cursoEmDestaque", videoDestaqueDto,
                "totalCursos", totalCursos
        );

        return ResponseEntity.ok(responseData);
    }
}