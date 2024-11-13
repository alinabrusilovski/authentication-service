package com.authservice.controller;

import com.authservice.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Scanner;

//@RestController

//@RequestMapping("/auth")
//@Controller
public class AuthController {
    private final AuthService authService;

//    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    public void start() {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("Choose an option: 1 - Register, 2 - Authenticate, 0 - Exit");
            int option = Integer.parseInt(scanner.nextLine());

            if (option == 1) {
                register(scanner);
            } else if (option == 2) {
                authenticate(scanner);
            } else if (option == 0) {
                System.out.println("Exiting the program");
                break;
            } else {
                System.out.println("Invalid option");
            }
        }
        scanner.close();
        System.exit(0);
    }

    private void register(Scanner scanner) {
        System.out.print("Enter email: ");
        String email = scanner.nextLine();

        System.out.print("Enter first name: ");
        String name = scanner.nextLine();

        System.out.print("Enter last name: ");
        String secondName = scanner.nextLine();

        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        authService.registerUser(email, name, secondName, password);
    }

    private void authenticate(Scanner scanner) {
        System.out.print("Enter email: ");
        String email = scanner.nextLine();

        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        int statusCode = authService.authenticate(email, password);

        if (statusCode == 200) {
            System.out.println("Authentication successful! Code: 200");
        } else if (statusCode == 400) {
            System.out.println("Error: Invalid username or password. Code: 400");
        }
    }
}