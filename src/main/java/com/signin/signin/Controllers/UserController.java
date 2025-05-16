package com.signin.signin.Controllers;

import com.signin.signin.Config.JwtService;
import com.signin.signin.Controllers.DTOs.LoginUserDto;
import com.signin.signin.Controllers.DTOs.UserDto;
import com.signin.signin.Models.UserModel;
import com.signin.signin.Services.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<?> save(@Valid @RequestBody UserDto user) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.userService.create(user.mapperToUserModel()));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginUserDto dto) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.login(dto.email(), dto.password()));
    }

    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("me")
    public ResponseEntity<?> get(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        String token = authHeader.substring(7);
        Long sub = jwtService.extractUserId(token);

        return ResponseEntity.status(HttpStatus.OK).body(userService.get(sub));
    }

    @SecurityRequirement(name = "bearerAuth")
    @PutMapping
    public ResponseEntity<?> update(@Valid @RequestBody UserDto user, HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        String token = authHeader.substring(7);
        Long sub = jwtService.extractUserId(token);

        return ResponseEntity.status(HttpStatus.OK).body(userService.update(user.mapperToUserModel(), sub));
    }

    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping
    public ResponseEntity<?> delete(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        String token = authHeader.substring(7);
        Long sub = jwtService.extractUserId(token);

        userService.delete(sub);
        return ResponseEntity.status(HttpStatus.OK).body("User deleted with success");
    }
}
