package com.hrrb.backend.security.services;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class GeradorDeSenha {
    public static void main(String[] args) {

        String senha = "@Leo2003";
        String hash = new BCryptPasswordEncoder().encode(senha);
        System.out.println("====================================================================");
        System.out.println("Senha original: " + senha);
        System.out.println("Hash gerado: " + hash);
        System.out.println("====================================================================");


        }
    }