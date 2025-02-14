package com.signin.signin.Services;

import com.signin.signin.Models.UserModel;
import com.signin.signin.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
@Service
public class UserService {

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

    public ResponseEntity<?> loginUser(UserModel user) {
        try {
            if (user == null || user.getEmail().isEmpty() || user.getPassword().isEmpty()) {
                return ResponseEntity.badRequest().body("Invalid user data: email and password fields are required.");
            }

            UserModel userFound = userRepository.findByEmail(user.getEmail());

            if (userFound == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
            }

            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            if (!passwordEncoder.matches(user.getPassword(), userFound.getPassword())) {
                return ResponseEntity.badRequest().body("Invalid password");
            }


            return ResponseEntity.ok(userFound);
        } catch (Exception e) {
            System.err.println("Error logging in: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while logging in.");
        }
    }

    @Transactional
    public ResponseEntity<?> deleteUser(Long id) {
        try {
            if (id == null || id == 0) {
                return ResponseEntity.badRequest().body("Invalid ID: cannot be null or 0.");
            }

            Optional<UserModel> user = userRepository.findById(id);

            if (user.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
            }

            userRepository.deleteUser(id);
            return ResponseEntity.ok("User deleted successfully");
        } catch (Exception e) {
            System.err.println("Error deleting user: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while deleting the user.");
        }
    }

    @Transactional
    public ResponseEntity<?> updateUser(UserModel user) {
        try {
            System.out.println("Updating user...");
            if (user == null || user.getName().isEmpty() || user.getEmail().isEmpty() || user.getPassword().isEmpty()) {
                System.out.println("Invalid user data: all fields are required.");
                return ResponseEntity.badRequest().body("Invalid user data: all fields are required.");
            }

            Optional<UserModel> userOld = userRepository.findById(user.getId());

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

            System.out.println("User updated with successed!!");
            return ResponseEntity.ok("User updated successfully");
        } catch (Exception e) {
            System.err.println("Error updating user: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while updating the user.");
        }
    }
}
