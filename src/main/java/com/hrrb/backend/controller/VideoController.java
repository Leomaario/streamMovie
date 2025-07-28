package com.hrrb.backend.controller;

import com.hrrb.backend.dto.UpdateVideoRequest;
import com.hrrb.backend.dto.VideoDTO;
import com.hrrb.backend.model.Catalogo;
import com.hrrb.backend.model.Video;
import com.hrrb.backend.repository.CatalogoRepository;
import com.hrrb.backend.repository.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/videos")
public class VideoController {

    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private CatalogoRepository catalogoRepository;

    @PostMapping
    public ResponseEntity<VideoDTO> createVideo(@RequestBody VideoDTO videoRequest) {
        return catalogoRepository.findById(videoRequest.getCatalogoId())
                .map(catalogo -> {
                    Video novoVideo = new Video();
                    novoVideo.setTitulo(videoRequest.getTitulo());
                    novoVideo.setDescricao(videoRequest.getDescricao());

                    // --- CORREÇÃO APLICADA AQUI ---
                    novoVideo.setVideoUrl(videoRequest.getUrlDoVideo()); // Usando o novo método setVideoUrl

                    novoVideo.setCatalogo(catalogo);
                    Video videoSalvo = videoRepository.save(novoVideo);
                    return new ResponseEntity<>(new VideoDTO(videoSalvo), HttpStatus.CREATED);
                })
                .orElse(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null));
    }

    @GetMapping
    public ResponseEntity<List<VideoDTO>> listarTodosVideos() {
        List<Video> videos = videoRepository.findAll();
        List<VideoDTO> videoDTOs = videos.stream().map(VideoDTO::new).collect(Collectors.toList());
        return ResponseEntity.ok(videoDTOs);
    }

    @GetMapping("/buscar/{id}")
    public ResponseEntity<VideoDTO> buscarVideoPorId(@PathVariable Long id) {
        return videoRepository.findById(id)
                .map(video -> ResponseEntity.ok(new VideoDTO(video)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<VideoDTO> atualizarVideo(
            @PathVariable Long id,
            @RequestBody UpdateVideoRequest request) {

        return videoRepository.findById(id)
                .map(videoExistente -> {
                    Catalogo novoCatalogo = catalogoRepository.findById(request.getCatalogoId())
                            .orElseThrow(() -> new RuntimeException("Erro: Catálogo de destino não encontrado."));

                    videoExistente.setTitulo(request.getTitulo());
                    videoExistente.setDescricao(request.getDescricao());
                    videoExistente.setCatalogo(novoCatalogo);

                    if (request.getUrlDoVideo() != null && !request.getUrlDoVideo().isEmpty()) {
                        // --- CORREÇÃO APLICADA AQUI TAMBÉM ---
                        videoExistente.setVideoUrl(request.getUrlDoVideo()); // Usando o novo método setVideoUrl
                    }

                    Video videoSalvo = videoRepository.save(videoExistente);
                    return ResponseEntity.ok(new VideoDTO(videoSalvo));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarVideo(@PathVariable Long id) {
        return videoRepository.findById(id)
                .map(video -> {
                    videoRepository.delete(video);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}