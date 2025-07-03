package com.hrrb.backend.controller;

import com.hrrb.backend.dto.UpdateUsuarioRequest;
import com.hrrb.backend.dto.UsuarioDTO;
import com.hrrb.backend.model.Grupo;
import com.hrrb.backend.model.Usuario;
import com.hrrb.backend.repository.GrupoRepository;
import com.hrrb.backend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private GrupoRepository grupoRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // --- READ (Listar todos os usuários) ---
    // Retorna uma lista de DTOs, e não a entidade completa, para não expor a senha.
    @GetMapping
    public ResponseEntity<List<UsuarioDTO>> listarTodosUsuarios() {
        // 1. Busca a lista completa de entidades Usuario do banco
        List<Usuario> usuarios = usuarioRepository.findAll();

        // 2. A linha mágica: transforma cada objeto Usuario em um objeto UsuarioDTO
        List<UsuarioDTO> dtos = usuarios.stream()
                .map(UsuarioDTO::new)
                .collect(Collectors.toList());

        // 3. Devolve a lista de DTOs, que o frontend saberá como ler
        return ResponseEntity.ok(dtos);
    }

    // --- READ (Buscar um usuário pelo ID) ---
    // Também retorna um DTO seguro.
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDTO> buscarUsuarioPorId(@PathVariable Long id) {
        return usuarioRepository.findById(id)
                .map(usuario -> ResponseEntity.ok(new UsuarioDTO(usuario)))
                .orElse(ResponseEntity.notFound().build());
    }

    // --- UPDATE (Atualizar um usuário existente) ---
    // Recebe um DTO específico para a atualização e retorna um DTO de resposta.
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioDTO> atualizarUsuario(@PathVariable Long id, @RequestBody UpdateUsuarioRequest request) {
        return usuarioRepository.findById(id)
                .map(usuarioExistente -> {
                    Grupo grupo = grupoRepository.findByNome(request.getGrupo())
                            .orElseThrow(() -> new RuntimeException("Erro: Grupo não encontrado."));

                    usuarioExistente.setNome(request.getNome());
                    usuarioExistente.setEmail(request.getEmail());
                    usuarioExistente.setPermissoes(request.getPermissoes());
                    usuarioExistente.setGrupo(grupo);

                    // Apenas atualiza a senha se uma nova for fornecida
                    if (request.getSenha() != null && !request.getSenha().trim().isEmpty()) {
                        usuarioExistente.setSenha(passwordEncoder.encode(request.getSenha()));
                    }

                    Usuario usuarioSalvo = usuarioRepository.save(usuarioExistente);
                    return ResponseEntity.ok(new UsuarioDTO(usuarioSalvo));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // --- DELETE (Deletar um usuário) ---
    // A lógica de apagar continua segura.
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarUsuario(@PathVariable Long id) {
        if (!usuarioRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        usuarioRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // NOTA: O método de CRIAR usuário (com criptografia) já está corretamente no seu AuthController.
    // Manter a criação de usuários na rota /api/auth/registrar é uma boa prática.
}