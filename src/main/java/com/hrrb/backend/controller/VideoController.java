package com.hrrb.backend.controller;

import com.hrrb.backend.dto.VideoDTO;
import com.hrrb.backend.model.Video;
import com.hrrb.backend.repository.CatalogoRepository;
import com.hrrb.backend.repository.VideoRepository;
import com.hrrb.backend.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource; // IMPORTAÇÃO CORRIGIDA
import org.springframework.core.io.Resource;
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

    // POST /api/videos - Cria um novo vídeo com upload de ficheiro
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

    // GET /api/videos - Lista todos os vídeos
    @GetMapping
    public ResponseEntity<List<VideoDTO>> listarTodosVideos() {
        List<Video> videos = videoRepository.findAll();
        List<VideoDTO> videoDTOs = videos.stream().map(VideoDTO::new).collect(Collectors.toList());
        return ResponseEntity.ok(videoDTOs);
    }

    // GET /api/videos/buscar/{id} - Busca um vídeo específico pelo ID
    @GetMapping("/buscar/{id}")
    public ResponseEntity<VideoDTO> buscarVideoPorId(@PathVariable Long id) {
        return videoRepository.findById(id)
                .map(video -> ResponseEntity.ok(new VideoDTO(video)))
                .orElse(ResponseEntity.notFound().build());
    }

    // PUT /api/videos/{id} - Atualiza os detalhes de um vídeo
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

    // DELETE /api/videos/{id} - Apaga um vídeo
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarVideo(@PathVariable Long id) {
        return videoRepository.findById(id)
                .map(video -> {
                    // fileStorageService.deleteFile(video.getCaminhoArquivo()); // TODO: Implementar no futuro
                    videoRepository.delete(video);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // GET /api/videos/{id}/stream - Faz o streaming do conteúdo de um vídeo
    @GetMapping("/{id}/stream")
    public ResponseEntity<Resource> streamVideo(@PathVariable Long id) {
        Optional<Video> videoData = videoRepository.findById(id);

        if (videoData.isPresent()) {
            Video video = videoData.get();
            try {
                String caminhoArquivo = video.getCaminhoArquivo();
                Path videoPath = Paths.get(caminhoArquivo);

                // Correção final: Usando FileSystemResource para ler o ficheiro do disco
                Resource videoResource = new FileSystemResource(videoPath);

                if (videoResource.exists() && videoResource.isReadable()) {
                    return ResponseEntity.ok()
                            .contentType(MediaType.valueOf("video/mp4"))
                            .body(videoResource);
                } else {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
                }
            } catch (Exception e) {
                System.err.println("Erro ao tentar fazer o streaming do ficheiro: " + e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
}
