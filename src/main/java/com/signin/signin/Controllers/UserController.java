package com.signin.signin.Controllers;

import com.signin.signin.Controllers.DTOs.LoginUserDto;
import com.signin.signin.Models.UserModel;
import com.signin.signin.Services.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserModel user) {
        return userService.createNewUser(user);
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@Valid @RequestBody LoginUserDto dto) {
        return userService.loginUser(dto.email(), dto.password());
    }

    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/{id}")
    public ResponseEntity<?> findUser(@PathVariable Long id) {
        return userService.findUser(id);
    }

    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/update")
    public ResponseEntity<?> updateUser(@Valid @RequestBody UserModel user) {
        return userService.updateUser(user);
    }

    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("deleteUser/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        return userService.deleteUser(id);
    }
}
