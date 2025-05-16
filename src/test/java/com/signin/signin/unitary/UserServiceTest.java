package com.signin.signin.unitary;

import com.signin.signin.Config.JwtService;
import com.signin.signin.Models.UserModel;
import com.signin.signin.Repositories.UserRepository;
import com.signin.signin.Services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authManager;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private UserService userService;

    @Captor
    private ArgumentCaptor<Long> longCaptor;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testIfUpdateUser() {
        UserModel existingUser = new UserModel();
        existingUser.setId(1L);
        existingUser.setName("Old Name");
        existingUser.setEmail("old@gmail.com");
        existingUser.setPassword("oldpassword");

        UserModel newUser = new UserModel();
        newUser.setName("Test of silva");
        newUser.setEmail("test@gmail.com");
        newUser.setPassword("12345678");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));

        when(passwordEncoder.encode(anyString())).thenReturn("passwordHash");

        when(userRepository.save(any(UserModel.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserModel userUpdated = userService.update(newUser, 1L);

        assertNotNull(userUpdated);
        assertEquals("Test of silva", userUpdated.getName());
        assertEquals("test@gmail.com", userUpdated.getEmail());
        assertEquals("passwordHash", userUpdated.getPassword());

        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(any(UserModel.class));
    }

    @Test
    public void testLoginSuccess() {
        String email = "test@gmail.com";
        String rawPassword = "123456";
        String tokenFake = "fake-jwt-token";

        UserModel user = new UserModel();
        user.setId(1L);
        user.setEmail(email);
        user.setPassword(rawPassword);

        UserDetails principal = mock(UserDetails.class);

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(principal);

        when(authManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        when(jwtService.generateToken(principal, user.getId())).thenReturn(tokenFake);

        Map<String, String> result = userService.login(email, rawPassword);

        assertNotNull(result);
        assertTrue(result.containsKey("token"));
        assertEquals(tokenFake, result.get("token"));

        verify(authManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository, times(1)).findByEmail(email);
        verify(jwtService, times(1)).generateToken(principal, user.getId());
    }

    @Test
    public void testIfSaveUser() {
        UserModel user = new UserModel();
        user.setName("test da silva");
        user.setEmail("test@gmail.com");
        user.setPassword("12345678");

        when(passwordEncoder.encode(anyString())).thenReturn("senhaCriptografada");

        when(userRepository.save(any(UserModel.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserModel created = this.userService.create(user);

        assertNotNull(created);
        assertInstanceOf(UserModel.class, created);
        assertEquals(created.getEmail(), user.getEmail());
        assertNotEquals("12345678", created.getPassword());

        verify(userRepository, times(1)).save(any(UserModel.class));
    }

    @Test
    public void testIfDeleteUser() {
        UserModel user = new UserModel();
        user.setId(1L);
        user.setName("Test of silva");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.delete(1L);

        verify(userRepository, times(1)).delete(user);
    }

    @Test
    public void testThrowBadRequest() {
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            this.userService.get(0L);
        });

        ResponseStatusException exception2 = assertThrows(ResponseStatusException.class, () -> {
            this.userService.get(-1L);
        });

        assertNotNull(exception);
        assertNotNull(exception2);
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals(HttpStatus.BAD_REQUEST, exception2.getStatusCode());

        verify(userRepository, times(0)).findById(1L);
    }

    @Test
    public void testThrowNotNull() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            this.userService.get(999L);
        });

        assertNotNull(exception);
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());

        verify(userRepository, times(1)).findById(999L);
    }

}
