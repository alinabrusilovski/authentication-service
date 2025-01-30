package com.authservice.service;

import com.authservice.dto.OperationResult;
import com.authservice.dto.UserDto;
import com.authservice.entity.UserEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface IAuthService {

    List<String> getScopesForUser(String email);

    boolean checkPassword(String email, String password) throws Exception;

    void initiatePasswordReset(String email, String captchaResponse) throws Exception;

    void resetPassword(String token, String newPassword) throws Exception;

    OperationResult<UserEntity> createUser(UserDto userDto) throws Exception;

    ResponseEntity<Object> generateAndReturnTokens(UserEntity user, List<String> scopes) throws Exception;
}
