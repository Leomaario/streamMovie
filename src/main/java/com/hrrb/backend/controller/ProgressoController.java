package com.hrrb.backend.controller;

import com.hrrb.backend.dto.MessageResponse;
import com.hrrb.backend.dto.VideoDTO;
import com.hrrb.backend.model.ProgressoUsuarioVideo;
import com.hrrb.backend.model.Usuario;
import com.hrrb.backend.model.Video;
import com.hrrb.backend.repository.ProgressoUsuarioVideoRepository;
import com.hrrb.backend.repository.UsuarioRepository;
import com.hrrb.backend.repository.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/progresso")
@CrossOrigin(origins = "*")
public class ProgressoController {

    @Autowired
    private ProgressoUsuarioVideoRepository progressoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private VideoRepository videoRepository;

    /**
     * Marca um vídeo como concluído para o utilizador autenticado.
     */
    @PostMapping("/{videoId}/marcar-concluido")
    public ResponseEntity<?> marcarVideoComoConcluido(
            @PathVariable Long videoId,
            Authentication authentication) {

        String username = authentication.getName();
        Usuario usuario = usuarioRepository.findByUsuario(username)
                .orElseThrow(() -> new RuntimeException("Erro: Utilizador não encontrado."));
        Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> new RuntimeException("Erro: Vídeo não encontrado."));

        ProgressoUsuarioVideo progresso = progressoRepository.findByUsuarioAndVideo(usuario, video)
                .orElse(new ProgressoUsuarioVideo());

        progresso.setUsuario(usuario);
        progresso.setVideo(video);
        progresso.setConcluido(true);
        progresso.setDataConclusao(LocalDateTime.now());

        progressoRepository.save(progresso);

        // Devolve uma resposta 200 OK com corpo vazio para evitar erros de serialização
        return ResponseEntity.ok().build();
    }

    /**
     * Verifica o status de conclusão de um vídeo para o utilizador autenticado.
     */
    @GetMapping("/{videoId}/status")
    public ResponseEntity<?> getProgressoStatus(
            @PathVariable Long videoId,
            Authentication authentication) {

        String username = authentication.getName();
        Usuario usuario = usuarioRepository.findByUsuario(username)
                .orElseThrow(() -> new RuntimeException("Erro: Utilizador não encontrado."));
        Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> new RuntimeException("Erro: Vídeo não encontrado."));

        boolean concluido = progressoRepository.findByUsuarioAndVideo(usuario, video)
                .map(ProgressoUsuarioVideo::isConcluido)
                .orElse(false);

        return ResponseEntity.ok(Map.of("concluido", concluido));
    }

    /**
     * Retorna uma lista de todos os vídeos marcados como concluídos pelo utilizador autenticado.
     */
    @GetMapping("/meus-concluidos")
    public ResponseEntity<List<VideoDTO>> getMeusCursosConcluidos(Authentication authentication) {
        String username = authentication.getName();
        Usuario usuario = usuarioRepository.findByUsuario(username)
                .orElseThrow(() -> new RuntimeException("Erro: Utilizador não encontrado."));

        List<VideoDTO> cursosConcluidos = progressoRepository.findByUsuarioAndConcluido(usuario, true)
                .stream()
                .map(progresso -> new VideoDTO(progresso.getVideo()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(cursosConcluidos);
    }
}