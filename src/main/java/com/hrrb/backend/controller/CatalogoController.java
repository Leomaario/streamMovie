package com.hrrb.backend.controller;

import com.hrrb.backend.model.Catalogo;
import com.hrrb.backend.repository.CatalogoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/catalogos") //AQUI É A URL FIXA DESSE CONTROLADOR
@CrossOrigin(origins = "*") // PERIMITE QUE QUALQUER FRONTEND ACESSE ESSA API
public class CatalogoController {

    @Autowired
    private CatalogoRepository catalogoRepository;

    //Get (Mostrar/Ler)
    @GetMapping
    public List<Catalogo> listarTodasOsCatalogos(){
        //aqui usamos o metodo que ganhamos do JpaRepository, que é o findAll();
      return catalogoRepository.findAll();
    }

    //POST(CRIAR/ADICIONAR)
    @PostMapping
    public Catalogo criarCatalogo(@RequestBody Catalogo novaCatalogo){
        return catalogoRepository.save(novaCatalogo);
    }


    //PUT (UPDATE/ATUALIZAR)



    @PutMapping("/{id}")
    public ResponseEntity<Catalogo> atualizarCategoria(
            @PathVariable Long id,
            @RequestBody Catalogo catalogoDetalhes){
        return catalogoRepository.findById(id)
                .map(catalogoExistente -> {
                    catalogoExistente.setNome(catalogoDetalhes.getNome());
                    catalogoExistente.setDescricao(catalogoDetalhes.getDescricao());
                    catalogoExistente.setIcone(catalogoDetalhes.getIcone());
                    catalogoExistente.setTag(catalogoDetalhes.getTag());

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
}
