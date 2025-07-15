package com.hrrb.backend.controller;

import com.hrrb.backend.dto.VideoDTO;
import com.hrrb.backend.model.Usuario;
import com.hrrb.backend.model.Video;
import com.hrrb.backend.repository.ProgressoUsuarioVideoRepository;
import com.hrrb.backend.repository.UsuarioRepository;
import com.hrrb.backend.repository.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/user-dashboard")
public class UserDashboardController {

    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ProgressoUsuarioVideoRepository progressoUsuarioVideoRepository;

    @GetMapping("/data")
    public ResponseEntity<?> getDashboardData(Authentication authentication) {
        String currentUserName = authentication.getName();
        Usuario usuario = usuarioRepository.findByUsuario(currentUserName)
                .orElseThrow(() -> new RuntimeException("Utilizador não encontrado."));
    
        // --- LÓGICA DE CÁLCULO DEFENSIVA ---
    
        long totalDeCursosNaPlataforma = videoRepository.count();
        long totalConcluidos = progressoUsuarioVideoRepository.countByUsuarioAndConcluido(usuario, true);
        long totalIniciados = progressoUsuarioVideoRepository.countByUsuario(usuario);
        long emAndamento = totalIniciados - totalConcluidos;
    
        int mediaConclusao = (totalDeCursosNaPlataforma > 0)
                ? (int) Math.round(((double) totalConcluidos / totalDeCursosNaPlataforma) * 100)
                : 0;
    
        Optional<Video> videoDestaqueOpt = videoRepository.findTopByOrderByIdDesc();
        VideoDTO videoDestaqueDto = videoDestaqueOpt.map(VideoDTO::new).orElse(null); // Continua seguro, retorna null se não houver vídeo
    
        // MUDANÇA PRINCIPAL: Usar um HashMap que aceita valores nulos
        Map<String, Object> responseData = new java.util.HashMap<>();
        responseData.put("cursoEmDestaque", videoDestaqueDto); // Agora, mesmo que videoDestaqueDto seja null, não há erro.
        responseData.put("totalCursos", totalDeCursosNaPlataforma);
        responseData.put("cursosEmAndamento", emAndamento);
        responseData.put("mediaConclusao", mediaConclusao);
        responseData.put("totalConcluidos", totalConcluidos); // Adicionei este para ser útil no frontend
    
        return ResponseEntity.ok(responseData);
    }
}
