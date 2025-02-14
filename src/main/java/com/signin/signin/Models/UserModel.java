package com.signin.signin.Models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Entity
@Table(name = "users")
@Data
public class UserModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull
    @Size(min = 3, max = 100, message = "O nome deve ter entre 3 e 100 caracteres.")
    private String name;

    @Email(message = "O email deve ser válido.")
    @NotNull
    @Column(unique = true, nullable = false, length = 255)
    private String email;

    @NotNull
    @Size(min = 6, message = "A senha deve ter pelo menos 6 caracteres.")
    private String password;

    public @Email(message = "O email deve ser válido.") @NotNull String getEmail() {
        return email;
    }

    public void setEmail(@Email(message = "O email deve ser válido.") @NotNull String email) {
        this.email = email;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public @NotNull @Size(min = 3, max = 100, message = "O nome deve ter entre 3 e 100 caracteres.") String getName() {
        return name;
    }

    public void setName(@NotNull @Size(min = 3, max = 100, message = "O nome deve ter entre 3 e 100 caracteres.") String name) {
        this.name = name;
    }

    public @NotNull @Size(min = 6, message = "A senha deve ter pelo menos 6 caracteres.") String getPassword() {
        return password;
    }

    public void setPassword(@NotNull @Size(min = 6, message = "A senha deve ter pelo menos 6 caracteres.") String password) {
        this.password = password;
    }
}
