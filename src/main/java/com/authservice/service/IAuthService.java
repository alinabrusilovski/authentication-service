package com.authservice.service;

import com.authservice.dto.OperationResult;
import com.authservice.dto.UserDto;
import com.authservice.entity.UserEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface IAuthService {

    List<String> getScopesForUser(String email);

    boolean checkPassword(String email, String password) throws Exception;

    void initiatePasswordReset(String email) throws Exception;

    void resetPassword(String token, String newPassword) throws Exception;

    OperationResult<UserEntity> createUser(UserDto userDto) throws Exception;
}
