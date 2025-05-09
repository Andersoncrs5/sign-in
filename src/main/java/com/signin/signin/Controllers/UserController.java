package com.signin.signin.Controllers;

import com.signin.signin.Config.JwtService;
import com.signin.signin.Controllers.DTOs.LoginUserDto;
import com.signin.signin.Models.UserModel;
import com.signin.signin.Services.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private JwtService jwtService;

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
    @GetMapping("me")
    public ResponseEntity<?> findUser(HttpServletRequest request) {

        String authHeader = request.getHeader("Authorization");
        String token = authHeader.substring(7);
        Long sub = jwtService.extractUserId(token);

        return userService.findUser(sub);
    }

    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/update")
    public ResponseEntity<?> updateUser(@Valid @RequestBody UserModel user, HttpServletRequest request) {

        String authHeader = request.getHeader("Authorization");
        String token = authHeader.substring(7);
        Long sub = jwtService.extractUserId(token);

        return userService.updateUser(user, sub);
    }

    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("deleteUser")
    public ResponseEntity<?> deleteUser(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        String token = authHeader.substring(7);
        Long sub = jwtService.extractUserId(token);

        return userService.deleteUser(sub);
    }
}
