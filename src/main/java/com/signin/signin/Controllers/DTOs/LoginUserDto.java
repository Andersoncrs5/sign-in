package com.signin.signin.Controllers.DTOs;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record LoginUserDto(
        @Email(message = "O email deve ser válido.")
        @NotNull
        @Column(unique = true, nullable = false, length = 255)
        String email,

        @NotNull
        @Size(min = 6, message = "A senha deve ter pelo menos 6 caracteres.")
        String password
) {
}
