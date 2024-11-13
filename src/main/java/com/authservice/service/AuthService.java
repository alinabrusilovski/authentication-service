package com.authservice.service;

import com.authservice.dto.UserDto;
import com.authservice.repository.UserRepository;
import com.authservice.security.PasswordHasher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

//@Component
public class AuthService implements IAuthService{

    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;

//    @Autowired
    public AuthService(UserRepository userRepository, PasswordHasher passwordHasher) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
    }

    public void registerUser(String email, String name, String secondName, String password) {
        try {
            // Генерация хеша пароля
            String hashedPassword = passwordHasher.generateHash(password);

            // Создание нового пользователя с хешем пароля
            UserDto newUser = new UserDto(null, name, secondName, email, hashedPassword, LocalDate.now(), LocalDate.now(), false);

            // Сохранение пользователя в "базу данных"
            userRepository.save(newUser);

            System.out.println("User registered successfully");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int authenticate(String username, String password) {
        var userOptional = userRepository.findByUsername(username);

        if (userOptional.isEmpty()) {
            return 400; // Пользователь не найден
        }

        UserDto user = userOptional.get();

        try {
            if (passwordHasher.checkHash(user.getPassword(), password)) {
                return 200; // Успешная аутентификация
            } else {
                return 400; // Неверный пароль
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 400;
        }
    }
}
