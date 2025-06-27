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

			@Bean
			public CommandLineRunner commandLineRunner(PasswordEncoder passwordEncoder) {
				return args -> {
					String rawPassword = "Leo123456";
					String encodedPassword = passwordEncoder.encode(rawPassword);

					System.out.println("====================================================================");
					System.out.println("PALAVRA-PASSE CRIPTOGRAFADA GERADA PELA SUA APLICAÇÃO:");
					System.out.println(encodedPassword);
					System.out.println("COPIE A PALAVRA-PASSE ACIMA E ATUALIZE O BANCO DE DADOS.");
					System.out.println("====================================================================");
				};

            };
	}
