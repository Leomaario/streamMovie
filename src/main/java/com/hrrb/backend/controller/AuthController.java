package com.hrrb.backend.controller;


import com.hrrb.backend.dto.JwtResponse;
import com.hrrb.backend.dto.LoginRequest;
import com.hrrb.backend.security.jwt.JwtUtils;
import com.hrrb.backend.security.services.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;


    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {

        // 1. O Spring Security tenta autenticar o usuário com os dados fornecidos
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsuario(), loginRequest.getSenha()));

        // 2. Se a autenticação deu certo, "loga" o usuário na sessão atual
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 3. Gera o "crachá digital" (token JWT)
        String jwt = jwtUtils.generateJwtToken(authentication);

        // 4. Pega os detalhes do usuário para retornar na resposta
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        // 5. Devolve o token e os dados do usuário para o frontend
        // A chamada aqui agora corresponde exatamente ao construtor da JwtResponse.
        return ResponseEntity.ok(new JwtResponse(jwt,
                userDetails.getId(),
                userDetails.getUsername(), // getUsername() retorna o campo 'usuario'
                userDetails.getEmail()));
    }

    @PostMapping("/registrar")
    public ResponseEntity<?> registerUser (/* @RequestBody RegisterRequest registerRequest */){
        return ResponseEntity.ok("Endpoint de registro funcionando ! :D");
    }

}

