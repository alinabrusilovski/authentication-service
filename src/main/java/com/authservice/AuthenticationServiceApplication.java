package com.authservice;

import com.authservice.controller.AuthController;
import com.authservice.repository.UserRepository;
import com.authservice.security.PasswordHasher;
import com.authservice.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


public class AuthenticationServiceApplication {
    public static void main(String[] args) throws Exception {
        UserRepository userRepository = new UserRepository();
        PasswordHasher passwordHasher = new PasswordHasher();
        AuthService authService = new AuthService(userRepository, passwordHasher);

        AuthController authController = new AuthController(authService);

        authController.start();
    }
}