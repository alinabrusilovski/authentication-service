package com.authservice.service;

import org.springframework.stereotype.Service;

@Service
public interface IAuthService {

    void registerUser(String email, String name, String secondName, String password);

    int authenticate(String username, String password);
}
