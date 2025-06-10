package com.hrrb.backend.controller;

import com.hrrb.backend.dto.VideoDTO;
import com.hrrb.backend.model.Catalogo;
import com.hrrb.backend.model.Video;
import com.hrrb.backend.repository.CatalogoRepository;
import com.hrrb.backend.repository.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

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


    // GET /api/videos - Lista todos os vídeos
    @GetMapping
    public ResponseEntity<List<VideoDTO>> listarTodosVideos() {
        List<Video> videos = videoRepository.findAll();
        // <<<--- MUDANÇA: Garantindo que o DTO seja usado para evitar erros de serialização ---<<<
        List<VideoDTO> videoDTOs = videos.stream().map(VideoDTO::new).collect(Collectors.toList());
        return ResponseEntity.ok(videoDTOs);
    }

    // GET /api/videos/{id} - Busca um vídeo por ID
    @GetMapping("/{id}")
    public ResponseEntity<VideoDTO> buscarVideoPorId(@PathVariable Long id) {
        // <<<--- MUDANÇA: Refatorado para ficar mais limpo e já retornar o DTO ---<<<
        return videoRepository.findById(id)
                .map(video -> ResponseEntity.ok(new VideoDTO(video)))
                .orElse(ResponseEntity.notFound().build());
    }

    // POST /api/videos - Cria um novo vídeo
    @PostMapping
    public ResponseEntity<VideoDTO> criarVideo(@RequestBody Video novoVideo) {
        if (novoVideo.getCatalogo() == null || novoVideo.getCatalogo().getId() == null) {
            return ResponseEntity.badRequest().build();
        }
        return catalogoRepository.findById(novoVideo.getCatalogo().getId())
                .map(catalogo -> {
                    novoVideo.setCatalogo(catalogo);
                    Video videoSalvo = videoRepository.save(novoVideo);
                    return new ResponseEntity<>(new VideoDTO(videoSalvo), HttpStatus.CREATED);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // PUT /api/videos/{id} - Atualiza um vídeo existente
    @PutMapping("/{id}") // <<<--- CORREÇÃO: A rota para ATUALIZAR é no ID do vídeo, não no /stream.
    public ResponseEntity<VideoDTO> atualizarVideo(@PathVariable Long id, @RequestBody Video videoDetalhes) {
        // <<<--- MUDANÇA: Refatorado para um estilo mais funcional e limpo ---<<<
        return videoRepository.findById(id)
                .map(videoExistente -> {
                    videoExistente.setTitulo(videoDetalhes.getTitulo());
                    videoExistente.setDescricao(videoDetalhes.getDescricao());
                    // Adicione outros campos para atualizar se necessário (ex: caminhoArquivo, duracaoSegundos)
                    Video videoSalvo = videoRepository.save(videoExistente);
                    return ResponseEntity.ok(new VideoDTO(videoSalvo));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // DELETE /api/videos/{id} - Deleta um vídeo
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deletarVideo(@PathVariable Long id) {
        // <<<--- MUDANÇA: Lógica simplificada e mais direta ---<<<
        if (!videoRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        videoRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // --- ENDPOINT DE STREAMING ---

    // GET /api/videos/{id}/stream - Pega o arquivo de vídeo para tocar no player
    @GetMapping("/{id}/stream") // <<<--- CORREÇÃO: Garantindo que a rota do GET para o stream esteja correta e única.
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
            long rangeLength = Math.min(1_048_576L, end - start + 1); // Envia no máximo 1MB por vez
            return new ResourceRegion(video, start, rangeLength);
        } else {
            long rangeLength = Math.min(1_048_576L, contentLength);
            return new ResourceRegion(video, 0, rangeLength);
        }
    }
}
