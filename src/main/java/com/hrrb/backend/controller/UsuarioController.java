package com.hrrb.backend.controller; // Adapte o nome do seu pacote

import com.hrrb.backend.model.Usuario;
import com.hrrb.backend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/usuarios") // URL base para a API de usuários
@CrossOrigin(origins = "*")
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    // --- 1. CREATE (Criar um novo usuário) ---
    @PostMapping
    public ResponseEntity<Usuario> criarUsuario(@RequestBody Usuario novoUsuario) {
        try {
            // Em um sistema real, aqui entraria a lógica para criptografar a senha
            // antes de salvar: novoUsuario.setSenha(passwordEncoder.encode(novoUsuario.getSenha()));
            Usuario usuarioSalvo = usuarioRepository.save(novoUsuario);
            return new ResponseEntity<>(usuarioSalvo, HttpStatus.CREATED);
        } catch (Exception e) {
            // Pode dar erro se o email já existir, por exemplo (por causa do 'UNIQUE' no banco)
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // --- 2. READ (Listar todos os usuários) ---
    @GetMapping
    public ResponseEntity<List<Usuario>> listarTodosUsuarios() {
        List<Usuario> usuarios = usuarioRepository.findAll();
        if (usuarios.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(usuarios, HttpStatus.OK);
    }

    // --- 3. READ (Buscar um usuário pelo ID) ---
    @GetMapping("/{id}")
    public ResponseEntity<Usuario> buscarUsuarioPorId(@PathVariable Long id) {
        return usuarioRepository.findById(id)
                .map(usuario -> ResponseEntity.ok(usuario))
                .orElse(ResponseEntity.notFound().build());
    }

    // --- 4. UPDATE (Atualizar um usuário existente) ---
    @PutMapping("/{id}")
    public ResponseEntity<Usuario> atualizarUsuario(@PathVariable Long id, @RequestBody Usuario usuarioDetalhes) {
        Optional<Usuario> usuarioData = usuarioRepository.findById(id);

        if (usuarioData.isPresent()) {
            Usuario _usuario = usuarioData.get();
            _usuario.setNome(usuarioDetalhes.getNome());
            _usuario.setEmail(usuarioDetalhes.getEmail());
            _usuario.setGrupo(usuarioDetalhes.getGrupo());
            _usuario.setPermissoes(usuarioDetalhes.getPermissoes());
            _usuario.setFotoPerfilPath(usuarioDetalhes.getFotoPerfilPath());

            // Lógica para não atualizar a senha se ela vier vazia no pedido
            if (usuarioDetalhes.getSenha() != null && !usuarioDetalhes.getSenha().isEmpty()) {
                // Novamente, aqui entraria a criptografia
                _usuario.setSenha(usuarioDetalhes.getSenha());
            }

            return new ResponseEntity<>(usuarioRepository.save(_usuario), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // --- 5. DELETE (Deletar um usuário) ---
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deletarUsuario(@PathVariable Long id) {
        try {
            if (!usuarioRepository.existsById(id)) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            usuarioRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            // Pode dar erro se o usuário tiver "links" em outras tabelas (certificados, etc.)
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
