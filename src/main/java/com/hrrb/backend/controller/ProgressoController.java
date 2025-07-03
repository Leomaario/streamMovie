package com.hrrb.backend.controller;

import com.hrrb.backend.dto.MessageResponse;
import com.hrrb.backend.model.ProgressoUsuarioVideo;
import com.hrrb.backend.model.Usuario;
import com.hrrb.backend.model.Video;
import com.hrrb.backend.repository.ProgressoUsuarioVideoRepository;
import com.hrrb.backend.repository.UsuarioRepository;
import com.hrrb.backend.repository.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

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

    @PostMapping(value = "/{videoId}/marcar-concluido", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> marcarVideoComoConcluido(
            @PathVariable Long videoId,
            Authentication authentication) {

        // 1. Pega o nome do utilizador a partir do token de autenticação
        String username = authentication.getName();

        // 2. Busca o objeto Usuario completo
        Usuario usuario = usuarioRepository.findByUsuario(username)
                .orElseThrow(() -> new RuntimeException("Erro: Utilizador não encontrado."));

        // 3. Busca o objeto Video completo
        Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> new RuntimeException("Erro: Vídeo não encontrado."));

        // 4. Verifica se já existe um registo de progresso. Se não existir, cria um novo.
        ProgressoUsuarioVideo progresso = progressoRepository.findByUsuarioAndVideo(usuario, video)
                .orElse(new ProgressoUsuarioVideo());

        // 5. Atualiza os dados do progresso
        progresso.setUsuario(usuario);
        progresso.setVideo(video);
        progresso.setConcluido(true);
        progresso.setDataConclusao(LocalDateTime.now());

        // 6. Salva o progresso (seja novo ou atualizado) no banco
        progressoRepository.save(progresso);

        return ResponseEntity.ok(new MessageResponse("Vídeo marcado como concluído com sucesso!"));
    }

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

        // Retorna um Map simples que será convertido para JSON
        return ResponseEntity.ok(Map.of("concluido", concluido));
    }
}