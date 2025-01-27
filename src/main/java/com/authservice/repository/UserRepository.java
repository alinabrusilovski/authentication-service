package com.authservice.repository;

import com.authservice.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Integer> {
    UserEntity findByEmail(String email);

    UserEntity findByRefreshToken(String refreshToken);

    UserEntity findByPasswordResetToken(String token);

}
