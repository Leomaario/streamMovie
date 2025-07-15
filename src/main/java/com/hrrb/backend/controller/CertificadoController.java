package com.hrrb.backend.controller;

import com.hrrb.backend.dto.CertificadoDTO;
import com.hrrb.backend.model.Usuario;
import com.hrrb.backend.repository.CertificadoRepository;
import com.hrrb.backend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/certificados")
public class CertificadoController {

    @Autowired
    private CertificadoRepository certificadoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    // Endpoint para buscar os certificados do utilizador logado
    @GetMapping("/meus")
    public ResponseEntity<List<CertificadoDTO>> getMeusCertificados(Authentication authentication) {
        // Pega o nome do utilizador a partir do token de autenticação
        String username = authentication.getName();

        // Busca o objeto Usuario completo
        Usuario usuario = usuarioRepository.findByUsuario(username)
                .orElseThrow(() -> new UsernameNotFoundException("Utilizador não encontrado: " + username));

        // Busca no repositório todos os certificados associados a esse utilizador
        List<CertificadoDTO> certificados = certificadoRepository.findByUsuario(usuario)
                .stream()
                .map(CertificadoDTO::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(certificados);
    }
}