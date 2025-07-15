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

    // REMOVIDO: A dependência do FileStorageService não é mais necessária.
    // @Autowired
    // private FileStorageService fileStorageService;


    // MUDANÇA: O endpoint de criação agora recebe um JSON simples, não mais um arquivo.
    @PostMapping
    public ResponseEntity<VideoDTO> createVideo(@RequestBody VideoDTO videoRequest) {
        // Busca o catálogo pelo ID fornecido no DTO
        return catalogoRepository.findById(videoRequest.getCatalogoId())
                .map(catalogo -> {
                    // Cria a nova entidade Video
                    Video novoVideo = new Video();
                    novoVideo.setTitulo(videoRequest.getTitulo());
                    novoVideo.setDescricao(videoRequest.getDescricao());
                    // Define a URL do vídeo recebida do frontend
                    novoVideo.setUrlDoVideo(videoRequest.getUrlDoVideo());
                    novoVideo.setCatalogo(catalogo);

                    // Salva o vídeo no banco de dados
                    Video videoSalvo = videoRepository.save(novoVideo);

                    // Retorna o DTO do vídeo criado
                    return new ResponseEntity<>(new VideoDTO(videoSalvo), HttpStatus.CREATED);
                })
                // Se o catálogo não for encontrado, retorna um erro.
                .orElse(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null));
    }


    // GET /api/videos - Lista todos os vídeos (Nenhuma mudança necessária aqui)
    @GetMapping
    public ResponseEntity<List<VideoDTO>> listarTodosVideos() {
        List<Video> videos = videoRepository.findAll();
        List<VideoDTO> videoDTOs = videos.stream().map(VideoDTO::new).collect(Collectors.toList());
        return ResponseEntity.ok(videoDTOs);
    }


    // GET /api/videos/buscar/{id} - Busca um vídeo específico (Nenhuma mudança necessária aqui)
    @GetMapping("/buscar/{id}")
    public ResponseEntity<VideoDTO> buscarVideoPorId(@PathVariable Long id) {
        return videoRepository.findById(id)
                .map(video -> ResponseEntity.ok(new VideoDTO(video)))
                .orElse(ResponseEntity.notFound().build());
    }


    // MUDANÇA: O endpoint de atualização agora também pode alterar a URL do vídeo.
    @PutMapping("/{id}")
    public ResponseEntity<VideoDTO> atualizarVideo(
            @PathVariable Long id,
            @RequestBody UpdateVideoRequest request) { // Assumindo que UpdateVideoRequest agora tem o campo 'urlDoVideo'

        return videoRepository.findById(id)
                .map(videoExistente -> {
                    Catalogo novoCatalogo = catalogoRepository.findById(request.getCatalogoId())
                            .orElseThrow(() -> new RuntimeException("Erro: Catálogo de destino não encontrado."));

                    videoExistente.setTitulo(request.getTitulo());
                    videoExistente.setDescricao(request.getDescricao());
                    videoExistente.setCatalogo(novoCatalogo);
                    // Adicionada a lógica para atualizar a URL do vídeo, se ela for fornecida.
                    if (request.getUrlDoVideo() != null && !request.getUrlDoVideo().isEmpty()) {
                        videoExistente.setUrlDoVideo(request.getUrlDoVideo());
                    }

                    Video videoSalvo = videoRepository.save(videoExistente);
                    return ResponseEntity.ok(new VideoDTO(videoSalvo));
                })
                .orElse(ResponseEntity.notFound().build());
    }


    // MUDANÇA: Lógica de apagar arquivo removida, pois não há mais arquivo.
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarVideo(@PathVariable Long id) {
        return videoRepository.findById(id)
                .map(video -> {
                    videoRepository.delete(video);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }


    // REMOVIDO: O endpoint de streaming foi completamente removido.
    // O streaming agora é responsabilidade do serviço externo (YouTube, Vimeo, etc.).
}