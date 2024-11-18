package com.authservice;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AuthenticationServiceApplication {

    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.load();

        SpringApplication.run(AuthenticationServiceApplication.class, args);
    }
}
