package com.hrrb.backend; // Garanta que este package é o mesmo das suas outras classes

import com.hrrb.backend.model.Grupo;
import com.hrrb.backend.model.Usuario;
import com.hrrb.backend.repository.GrupoRepository;
import com.hrrb.backend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private GrupoRepository grupoRepository;

    @Autowired
    private PasswordEncoder encoder;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("======================================================");
        System.out.println(">>> DataLoader: A verificar dados iniciais...");

        // --- CRIA O GRUPO 'ADMINISTRADORES' SE NÃO EXISTIR ---
        String nomeGrupoAdmin = "Administradores";
        if (grupoRepository.findByNome(nomeGrupoAdmin).isEmpty()) {
            System.out.println(">>> Grupo '" + nomeGrupoAdmin + "' não encontrado. A criar...");
            Grupo grupoAdmin = new Grupo();
            grupoAdmin.setNome(nomeGrupoAdmin);
            grupoRepository.save(grupoAdmin);
            System.out.println(">>> Grupo '" + nomeGrupoAdmin + "' criado com sucesso.");
        }

        // --- CRIA O USUÁRIO 'ADMIN' SE NÃO EXISTIR ---
        String adminUsername = "admin";
        if (usuarioRepository.findByUsuario(adminUsername).isEmpty()) {
            System.out.println(">>> Usuário '" + adminUsername + "' não encontrado. A criar...");

            // Pega o grupo que acabamos de garantir que existe
            Grupo adminGrupo = grupoRepository.findByNome(nomeGrupoAdmin).get();

            Usuario admin = new Usuario();
            admin.setNome("Administrador Principal");
            admin.setUsuario(adminUsername);
            admin.setEmail("admin@souzalink.com");
            admin.setSenha(encoder.encode("admin123")); // <-- Anote esta senha!
            admin.setGrupo(adminGrupo);
            admin.setPermissoes("ROLE_ADMIN");

            usuarioRepository.save(admin);
            System.out.println(">>> Usuário '" + adminUsername + "' criado com sucesso!");
        } else {
            System.out.println(">>> Usuário '" + adminUsername + "' já existe. Nenhum dado foi alterado.");
        }

        System.out.println(">>> DataLoader: Verificação concluída.");
        System.out.println("======================================================");
    }
}