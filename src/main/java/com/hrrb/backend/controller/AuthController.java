package com.hrrb.backend.controller;

import com.hrrb.backend.dto.JwtResponse;
import com.hrrb.backend.dto.LoginRequest;
import com.hrrb.backend.dto.MessageResponse;
import com.hrrb.backend.dto.RegistroRequest;
import com.hrrb.backend.model.Grupo;
import com.hrrb.backend.model.Usuario;
import com.hrrb.backend.repository.GrupoRepository;
import com.hrrb.backend.repository.UsuarioRepository;
import com.hrrb.backend.security.jwt.JwtUtils;
import com.hrrb.backend.security.services.UserDetailsImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "https://api-e-learning-gjnd.onrender.com")
public class AuthController {


    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    UsuarioRepository usuarioRepository;

    @Autowired
    GrupoRepository grupoRepository;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsuario(), loginRequest.getSenha()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        // --- BLOCO DE DEPURAÇÃO FINAL ---
        logger.info("=======================================");
        logger.info("INSPETOR DE LOGIN PARA O UTILIZADOR: {}", userDetails.getUsername());
        logger.info("ID DO UTILIZADOR: {}", userDetails.getId());
        logger.info("PERMISSÕES CARREGADAS DO BANCO: {}", roles);
        logger.info("A ENVIAR ESTA RESPOSTA PARA O FRONTEND...");
        logger.info("=======================================");
        // ------------------------------------

        return ResponseEntity.ok(new JwtResponse(jwt, userDetails.getId(), userDetails.getUsername(), userDetails.getEmail(), roles));
    }

    @PostMapping(value = "/registrar", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> registerUser(@RequestBody RegistroRequest registroRequest) {
        if (usuarioRepository.existsByUsuario(registroRequest.getUsuario())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Erro: Nome de usuário já está em uso!"));
        }
        if (usuarioRepository.existsByEmail(registroRequest.getEmail())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Erro: Email já está em uso!"));
        }

        Grupo grupo = grupoRepository.findByNome(registroRequest.getGrupo())
                .orElseThrow(() -> new RuntimeException("Erro: Grupo '" + registroRequest.getGrupo() + "' não encontrado."));

        Usuario usuario = new Usuario();
        usuario.setNome(registroRequest.getNome());
        usuario.setUsuario(registroRequest.getUsuario());
        usuario.setEmail(registroRequest.getEmail());
        usuario.setSenha(encoder.encode(registroRequest.getSenha()));
        usuario.setGrupo(grupo);
        usuario.setPermissoes(registroRequest.getPermissoes());

        usuarioRepository.save(usuario);

        return ResponseEntity.ok(new MessageResponse("Usuário registrado com sucesso!"));
    }
}