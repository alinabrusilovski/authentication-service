package com.authservice.service;

import org.springframework.stereotype.Service;

@Service
public interface IAuthService {

    boolean checkPassword(String email, String password) throws Exception;

}
