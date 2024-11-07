package com.authservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.lang.Nullable;

public class ScopeDto{
    @Nullable
    private Integer id;

    @NotBlank(message = "Scope name cannot be blank")
    @Size(max = 100, message = "Scope name cannot exceed 100 characters")
    private String name;

    private String description;

}

