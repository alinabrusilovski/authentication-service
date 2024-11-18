package com.authservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.lang.Nullable;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ScopeDto{
    @Nullable
    private Integer id;

    @NotBlank(message = "Scope name cannot be blank")
    @Size(max = 100, message = "Scope name cannot exceed 100 characters")
    private String name;

    private String description;

}

