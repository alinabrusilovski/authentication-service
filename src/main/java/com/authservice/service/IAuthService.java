package com.authservice.service;

import com.authservice.dto.UserDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface IAuthService {

    List<String> getScopesForUser(String email);

    boolean checkPassword(String email, String password) throws Exception;

    void initiatePasswordReset(String email);

    void resetPassword(String token, String newPassword) throws Exception;

    void createUser(UserDto userDto) throws Exception;
}
