package com.hrrb.backend.controller;

import com.hrrb.backend.dto.UpdateUsuarioRequest;
import com.hrrb.backend.dto.UsuarioDTO;
import com.hrrb.backend.model.Usuario;
import com.hrrb.backend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
@CrossOrigin(origins = "*")
public class ProfileController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    // Não precisamos do PasswordEncoder aqui, a senha será atualizada no futuro

    // GET /api/profile/me - Busca os dados do utilizador logado
    @GetMapping("/me")
    public ResponseEntity<UsuarioDTO> getMeuPerfil(Authentication authentication) {
        String username = authentication.getName();
        return usuarioRepository.findByUsuario(username)
                .map(usuario -> ResponseEntity.ok(new UsuarioDTO(usuario)))
                .orElse(ResponseEntity.notFound().build());
    }

    // PUT /api/profile/me - Atualiza os dados do utilizador logado
    @PutMapping("/me")
    public ResponseEntity<UsuarioDTO> atualizarMeuPerfil(@RequestBody UpdateUsuarioRequest request, Authentication authentication) {
        String username = authentication.getName();
        return usuarioRepository.findByUsuario(username)
                .map(usuarioExistente -> {
                    usuarioExistente.setNome(request.getNome());
                    usuarioExistente.setEmail(request.getEmail());


                    Usuario usuarioSalvo = usuarioRepository.save(usuarioExistente);
                    return ResponseEntity.ok(new UsuarioDTO(usuarioSalvo));
                })
                .orElse(ResponseEntity.notFound().build());
    }
}