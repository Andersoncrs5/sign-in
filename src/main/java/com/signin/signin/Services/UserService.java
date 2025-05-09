package com.signin.signin.Services;

import com.signin.signin.Config.JwtService;
import com.signin.signin.Models.UserModel;
import com.signin.signin.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Map;
import java.util.Optional;
@Service
public class UserService {

    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    public ResponseEntity<?> findUser(Long id) {
        try {
            if (id == null || id == 0) {
                return ResponseEntity.badRequest().body("Invalid ID: cannot be null or 0.");
            }

            Optional<UserModel> user = userRepository.findById(id);

            if (user.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
            }

            return ResponseEntity.ok(user.get());
        } catch (Exception e) {
            System.err.println("Error finding user: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while finding the user.");
        }
    }

    @Transactional
    public ResponseEntity<?> createNewUser(UserModel user) {
        try {
            if (user == null || user.getName().isEmpty() || user.getEmail().isEmpty() || user.getPassword().isEmpty()) {
                return ResponseEntity.badRequest().body("Invalid user data: all fields are required.");
            }

            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            user.setPassword(passwordEncoder.encode(user.getPassword()));

            UserModel savedUser = userRepository.save(user);

            return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
        } catch (Exception e) {
            System.err.println("Error creating user: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while creating the user.");
        }
    }

    public ResponseEntity<?> loginUser(String email, String password) {
        if (email == null || password == null || email.isBlank() || password.isBlank()) {
            return ResponseEntity.badRequest().body("Email and password are required.");
        }

        try {
            Authentication authentication = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );

            Optional<UserModel> optionalUser = this.userRepository.findByEmail(email);

            if (optionalUser.isEmpty())
                return new ResponseEntity<>("",HttpStatus.UNAUTHORIZED);

            UserModel user = optionalUser.get();

            String token = jwtService.generateToken((UserDetails) authentication.getPrincipal(), user.getId());
            return ResponseEntity.ok(Map.of("token", token));

        } catch (AuthenticationException ex) {
            System.out.println(ex);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials.");
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An internal error occurred.");
        }
    }

    @Transactional
    public ResponseEntity<?> deleteUser(Long id) {
        try {
            this.findUser(id);

            userRepository.deleteUser(id);
            return ResponseEntity.ok("User deleted successfully");
        } catch (Exception e) {
            System.err.println("Error deleting user: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while deleting the user.");
        }
    }

    @Transactional
    public ResponseEntity<?> updateUser(UserModel user, Long id) {
        try {
            if (id == null) {
                return ResponseEntity.badRequest().body("Invalid id");
            }

            Optional<UserModel> userOld = userRepository.findById(id);

            if (userOld.isEmpty()) {
                System.out.println("User not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
            }

            UserModel updatedUser = userOld.get();
            updatedUser.setName(user.getName());
            updatedUser.setEmail(user.getEmail());
            updatedUser.setPassword(user.getPassword());

            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            updatedUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));

            userRepository.save(updatedUser);

            return ResponseEntity.ok("User updated successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while updating the user.");
        }
    }
}
