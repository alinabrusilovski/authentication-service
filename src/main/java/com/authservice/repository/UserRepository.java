package com.authservice.repository;

import com.authservice.dto.UserDto;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

//@Repository
public class UserRepository {

    //"база данных" для консольного приложения
    private final Map<String, UserDto> users = new HashMap<>();

    public Optional<UserDto> findByUsername(String username) {
        return Optional.ofNullable(users.get(username));
    }

    public void save(UserDto user) {
        users.put(user.getEmail(), user);
    }
}
