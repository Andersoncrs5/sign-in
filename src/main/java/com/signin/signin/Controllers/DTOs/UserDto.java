package com.signin.signin.Controllers.DTOs;

import com.signin.signin.Models.UserModel;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserDto {

    @NotNull
    @Size(min = 3, max = 50)
    private String name;

    @NotNull
    @Email
    @Size(min = 10, max = 150)
    private String email;

    @NotNull
    @Size(min = 8, max = 50)
    private String password;

    public UserModel mapperToUserModel() {
        UserModel user = new UserModel();

        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);

        return user;
    }
}
