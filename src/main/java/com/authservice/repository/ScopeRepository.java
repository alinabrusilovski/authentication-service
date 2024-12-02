package com.authservice.repository;

import com.authservice.entity.ScopeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScopeRepository extends JpaRepository<ScopeEntity, Integer> {
    ScopeEntity findByName(String name);
}
