package com.hrrb.backend.controller;

import com.hrrb.backend.dto.VideoDTO;
import com.hrrb.backend.model.Catalogo;
import com.hrrb.backend.repository.CatalogoRepository;
import com.hrrb.backend.repository.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/catalogos")
@CrossOrigin(origins = "*")
public class CatalogoController {

    @Autowired
    private CatalogoRepository catalogoRepository;

    // --- DEPENDÊNCIA ADICIONADA ---
    // Precisamos do repositório de vídeos para buscar a playlist
    @Autowired
    private VideoRepository videoRepository;

    @GetMapping
    public List<Catalogo> listarTodasOsCatalogos(){
        return catalogoRepository.findAll();
    }

    @PostMapping
    public Catalogo criarCatalogo(@RequestBody Catalogo novaCatalogo){
        return catalogoRepository.save(novaCatalogo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Catalogo> atualizarCategoria(
            @PathVariable Long id,
            @RequestBody Catalogo catalogoDetalhes){
        return catalogoRepository.findById(id)
                .map(catalogoExistente -> {
                    catalogoExistente.setNome(catalogoDetalhes.getNome());
                    catalogoExistente.setDescricao(catalogoDetalhes.getDescricao());
                    // Adicionei os outros campos que você tinha
                    catalogoExistente.setIcone(catalogoDetalhes.getIcone());
                    catalogoExistente.setTag(catalogoDetalhes.getTag());
                    catalogoExistente.setCaminhoPasta(catalogoDetalhes.getCaminhoPasta());

                    Catalogo catalogoAtualizada = catalogoRepository.save(catalogoExistente);
                    return ResponseEntity.ok(catalogoAtualizada);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarCategoria(@PathVariable Long id){
        if (!catalogoRepository.existsById(id)){
            return ResponseEntity.notFound().build();
        }
        catalogoRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // --- MÉTODO NOVO PARA A PLAYLIST ---
    @GetMapping("/{catalogoId}/videos")
    public ResponseEntity<List<VideoDTO>> listarVideosPorCatalogo(@PathVariable Long catalogoId) {
        // Usa o VideoRepository para encontrar todos os vídeos com o ID do catálogo
        List<VideoDTO> videos = videoRepository.findByCatalogoId(catalogoId)
                .stream()
                .map(VideoDTO::new) // Converte cada Video em um VideoDTO
                .collect(Collectors.toList());
        return ResponseEntity.ok(videos);
    }
}