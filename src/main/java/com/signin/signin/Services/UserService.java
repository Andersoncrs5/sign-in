package com.signin.signin.Services;

import com.signin.signin.Config.JwtService;
import com.signin.signin.Models.UserModel;
import com.signin.signin.Repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.server.ResponseStatusException;

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

    @Async
    @Transactional
    public UserModel get(Long id) {
        if (id == null || id <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Id is required");
        }

        UserModel user = userRepository.findById(id).orElse(null);

        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }

        return user;
    }

    @Async
    @Transactional
    public UserModel create(UserModel user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        return this.userRepository.save(user);
    }

    @Async
    public Map<String, String> login(String email, String password) {
        Authentication authentication = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );

        Optional<UserModel> optionalUser = this.userRepository.findByEmail(email);

        if (optionalUser.isEmpty())
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);

        UserModel user = optionalUser.get();

        String token = jwtService.generateToken((UserDetails) authentication.getPrincipal(), user.getId());
        return Map.of("token", token);
    }

    @Async
    @Transactional
    public void delete(Long id) {
        var user = this.get(id);
        this.userRepository.delete(user);
    }

    @Async
    @Transactional
    public UserModel update(UserModel user, Long id) {
        UserModel updatedUser = this.get(id);

        updatedUser.setName(user.getName());
        updatedUser.setEmail(user.getEmail());
        updatedUser.setPassword(user.getPassword());

        updatedUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));

        return this.userRepository.save(updatedUser);
    }
}
