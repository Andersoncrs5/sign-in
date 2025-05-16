package com.signin.signin.Controllers.DTOs;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record LoginUserDto(
        @Email
        @NotNull
        @Size(min = 10, max = 150)
        String email,

        @NotNull
        @Size(min = 6, max = 50)
        String password
) {
}
