package com.signin.signin.validation;

import com.signin.signin.Controllers.DTOs.UserDto;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class UserDtoValidationTest {

    private Validator validator;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void shouldPassValidationWithValidData() {
        UserDto dto = new UserDto();
        dto.setName("John Doe");
        dto.setEmail("john.doe@example.com");
        dto.setPassword("securePass123");

        Set<ConstraintViolation<UserDto>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    public void shouldFailValidationWithInvalidEmail() {
        UserDto dto = new UserDto();
        dto.setName("John Doe");
        dto.setEmail("invalid-email");
        dto.setPassword("securePass123");

        Set<ConstraintViolation<UserDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());

        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email")));
    }

    @Test
    public void shouldFailValidationWhenNameIsTooShort() {
        UserDto dto = new UserDto();
        dto.setName("Jo");
        dto.setEmail("john.doe@example.com");
        dto.setPassword("securePass123");

        Set<ConstraintViolation<UserDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());

        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("name")));
    }

    @Test
    public void shouldFailValidationWhenPasswordIsNull() {
        UserDto dto = new UserDto();
        dto.setName("John Doe");
        dto.setEmail("john.doe@example.com");
        dto.setPassword(null);

        Set<ConstraintViolation<UserDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());

        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("password")));
    }
}
