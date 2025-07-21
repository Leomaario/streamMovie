package com.hrrb.backend.controller;

import com.hrrb.backend.dto.JwtResponse;
import com.hrrb.backend.dto.LoginRequest;
import com.hrrb.backend.dto.MessageResponse;
import com.hrrb.backend.dto.RegistroRequest;

import com.hrrb.backend.exception.ResourceNotFoundException;
import com.hrrb.backend.model.Grupo;
import com.hrrb.backend.model.Usuario;
import com.hrrb.backend.repository.GrupoRepository;
import com.hrrb.backend.repository.UsuarioRepository;
import com.hrrb.backend.security.jwt.JwtUtils;
import com.hrrb.backend.security.services.UserDetailsImpl;


import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;



@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "https://souzalink-coach.onrender.com")
@RequiredArgsConstructor
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder encoder;
    private final JwtUtils jwtUtils;
    private final UsuarioRepository usuarioRepository;
    private final GrupoRepository grupoRepository;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsuario(), loginRequest.getSenha())

            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.generateJwtToken(authentication);
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

            List<String> roles = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());

            logLoginAttempt(userDetails, roles);
            return ResponseEntity.ok(new JwtResponse(
                    jwt,
                    userDetails.getId(),
                    userDetails.getUsername(),
                    userDetails.getEmail(),
                    roles
            ));

        } catch (BadCredentialsException e) {
            logger.warn("Falha na tentativa de login para usuário: {}", loginRequest.getUsuario());
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new MessageResponse("Credenciais inválidas. Por favor, verifique seu usuário e senha."));
        } catch (Exception e) {
            logger.error("Erro durante autenticação", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Erro no servidor. Por favor, tente novamente mais tarde."));
        }
    }

    @PostMapping(value = "/registrar", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> registerUser(@RequestBody RegistroRequest registroRequest) {
        try {
            // Validação de usuário existente
            if (usuarioRepository.existsByUsuario(registroRequest.getUsuario())) {
                return ResponseEntity
                        .badRequest()
                        .body(new MessageResponse("Este nome de usuário já está em uso."));
            }

            // Validação de email existente
            if (usuarioRepository.existsByEmail(registroRequest.getEmail())) {
                return ResponseEntity
                        .badRequest()
                        .body(new MessageResponse("Este email já está registrado."));
            }

            // Busca do grupo
            Grupo grupo = grupoRepository.findByNome(registroRequest.getGrupo())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Grupo não encontrado: " + registroRequest.getGrupo()
                    ));

            // Criação do usuário
            Usuario usuario = createNewUser(registroRequest, grupo);
            usuarioRepository.save(usuario);

            logger.info("Novo usuário registrado com sucesso: {}", usuario.getUsuario());
            return ResponseEntity.ok(new MessageResponse("Usuário registrado com sucesso!"));

        } catch (ResourceNotFoundException e) {
            logger.error("Erro ao registrar usuário - grupo não encontrado: {}", e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new MessageResponse(e.getMessage()));
        } catch (Exception e) {
            logger.error("Erro ao registrar usuário: {}", e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Erro ao processar o registro. Por favor, tente novamente."));
        }

    }

    private void logLoginAttempt(UserDetailsImpl userDetails, List<String> roles) {
        logger.info("=======================================");
        logger.info("Login bem-sucedido para usuário: {}", userDetails.getUsername());
        logger.info("ID do usuário: {}", userDetails.getId());
        logger.info("Permissões: {}", roles);
        logger.info("=======================================");
    }

    private Usuario createNewUser(RegistroRequest request, Grupo grupo) {
        Usuario usuario = new Usuario();
        usuario.setNome(request.getNome());
        usuario.setUsuario(request.getUsuario());
        usuario.setEmail(request.getEmail());
        usuario.setSenha(encoder.encode(request.getSenha()));
        usuario.setGrupo(grupo);
        usuario.setPermissoes(request.getPermissoes());
        return usuario;
    }
}