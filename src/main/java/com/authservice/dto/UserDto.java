package com.authservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserDto {
    private Integer id;
    private String name;
    private String secondName;
    @Email(message = "Invalid email format")
    @NotBlank(message = "Email cannot be blank")
    @Size(max = 255, message = "Email cannot exceed 255 characters")
    private String email;

    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String password;

    private LocalDate created;

    private LocalDate updated;

    private Boolean deleted;

    public void setEmail(String email) {
        this.email = (email != null) ? email.toLowerCase() : null;
    }
}

