package com.hrrb.backend;

import org.springframework.boot.CommandLineRunner; // Adicione este import
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean; // Adicione este import
import org.springframework.security.crypto.password.PasswordEncoder; // Adicione este import

@SpringBootApplication
public class BackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
	}

	// --- ADICIONE ESTE MÉTODO AQUI DENTRO DA CLASSE ---
	@Bean
	public CommandLineRunner commandLineRunner(PasswordEncoder passwordEncoder) {
		return args -> {

			System.out.println("====================================================================");
			System.out.println("CRUD RODANDO A MIL POR HORA !");
			System.out.println("====================================================================");
		};
	}
}