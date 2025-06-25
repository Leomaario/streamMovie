package com.hrrb.backend.controller;

import com.hrrb.backend.dto.VideoDTO;
import com.hrrb.backend.model.Catalogo;
import com.hrrb.backend.model.Video;
import com.hrrb.backend.repository.CatalogoRepository;
import com.hrrb.backend.repository.VideoRepository;
import com.hrrb.backend.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/videos")
@CrossOrigin(origins = "*")
public class VideoController {

    @Autowired
    private VideoRepository videoRepository;
    @Autowired
    private CatalogoRepository catalogoRepository;
    @Autowired
    private FileStorageService fileStorageService;


    @PostMapping(consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<VideoDTO> uploadAndCreateVideo(
            @RequestParam("file") MultipartFile file,
            @RequestParam("titulo") String titulo,
            @RequestParam("descricao") String descricao,
            @RequestParam("catalogoId") Long catalogoId) {

        return catalogoRepository.findById(catalogoId)
                .map(catalogo -> {
                    String caminhoArquivo = fileStorageService.storeFile(file, catalogoId);

                    Video novoVideo = new Video();
                    novoVideo.setTitulo(titulo);
                    novoVideo.setDescricao(descricao);
                    novoVideo.setCatalogo(catalogo);
                    novoVideo.setCaminhoArquivo(caminhoArquivo);

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

    @GetMapping("/{id}")
    public ResponseEntity<VideoDTO> buscarVideoPorId(@PathVariable Long id) {
        return videoRepository.findById(id)
                .map(video -> ResponseEntity.ok(new VideoDTO(video)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<VideoDTO> atualizarVideo(@PathVariable Long id, @RequestBody Video videoDetalhes) {
        return videoRepository.findById(id)
                .map(videoExistente -> {
                    videoExistente.setTitulo(videoDetalhes.getTitulo());
                    videoExistente.setDescricao(videoDetalhes.getDescricao());
                    Video videoSalvo = videoRepository.save(videoExistente);
                    return ResponseEntity.ok(new VideoDTO(videoSalvo));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deletarVideo(@PathVariable Long id) {
        if (!videoRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        videoRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // --- ENDPOINT DE STREAMING ---

    @GetMapping("/{id}/stream")
    public ResponseEntity<ResourceRegion> streamVideo(@RequestHeader HttpHeaders headers, @PathVariable Long id) {
        Optional<Video> videoData = videoRepository.findById(id);
        if (videoData.isEmpty()) {
            System.out.println(">>> STREAM ERRO: Vídeo com ID " + id + " não encontrado no banco.");
            return ResponseEntity.notFound().build();
        }

        try {
            String caminhoArquivo = videoData.get().getCaminhoArquivo();
            System.out.println(">>> STREAM DEBUG: Tentando carregar vídeo de: " + caminhoArquivo);

            Path videoPath = Paths.get(caminhoArquivo);
            UrlResource videoResource = new UrlResource(videoPath.toUri());

            if (!videoResource.exists() || !videoResource.isReadable()) {
                System.out.println(">>> STREAM ERRO: Arquivo não existe ou não pode ser lido em: " + caminhoArquivo);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            System.out.println(">>> STREAM SUCESSO: Arquivo encontrado! Transmitindo...");
            ResourceRegion region = resourceRegion(videoResource, headers);

            return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                    .contentType(MediaType.valueOf("video/mp4"))
                    .body(region);
        } catch (Exception e) {
            System.out.println(">>> STREAM EXCEÇÃO: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Método auxiliar para o streaming
    private ResourceRegion resourceRegion(UrlResource video, HttpHeaders headers) throws Exception {
        long contentLength = video.contentLength();
        Optional<HttpRange> range = headers.getRange().stream().findFirst();
        if (range.isPresent()) {
            long start = range.get().getRangeStart(contentLength);
            long end = range.get().getRangeEnd(contentLength);
            long rangeLength = Math.min(1_048_576L, end - start + 1);
            return new ResourceRegion(video, start, rangeLength);
        } else {
            long rangeLength = Math.min(1_048_576L, contentLength);
            return new ResourceRegion(video, 0, rangeLength);
        }
    }
}
