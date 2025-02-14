package com.signin.signin.Controllers;

import com.signin.signin.Models.UserModel;
import com.signin.signin.Services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/")
    public ResponseEntity<?> home() {
        return ResponseEntity.ok("Server is running");
    }

    //curl -X POST http://localhost:9090/users/register \ -H "Content-Type: application/json" \ -d "{\"id\":null,\"name\":\"test1\",\"email\":\"test1@example.com\",\"password\":\"12345678\"}"
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserModel user) {
        return userService.createNewUser(user);
    }

    //curl -X POST http://localhost:9090/users/login \ -H "Content-Type: application/json" \ -d "{\"id\":1,\"name\":\"test1 att\",\"email\":\"test1@example.com\",\"password\":\"12345678\"}"
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@Valid @RequestBody UserModel user) {
        return userService.loginUser(user);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findUser(@PathVariable Long id) {
        return userService.findUser(id);
    }

    //curl -X PUT http://localhost:9090/users/update \ -H "Content-Type: application/json" \ -d "{\"id\":1,\"name\":\"test1 att\",\"email\":\"test1@example.com\",\"password\":\"12345678\"}"
    @PutMapping("/update")
    public ResponseEntity<?> updateUser(@Valid @RequestBody UserModel user) {
        return userService.updateUser(user);
    }

    //curl -X DELETE http://localhost:9090/users/deleteUser/1
    @DeleteMapping("deleteUser/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        return userService.deleteUser(id);
    }
}
