package com.saudecardiaca.service;

import com.saudecardiaca.dto.request.LoginRequest;
import com.saudecardiaca.dto.request.RegisterRequest;
import com.saudecardiaca.dto.response.LoginResponse;
import com.saudecardiaca.dto.response.UserResponse;
import com.saudecardiaca.exception.ApiException;
import com.saudecardiaca.model.User;
import com.saudecardiaca.repository.UserRepository;
import com.saudecardiaca.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private User user;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest();
        registerRequest.setFirstName("Ana");
        registerRequest.setLastName("Silva");
        registerRequest.setEmail("ana@email.com");
        registerRequest.setPhone("+55 11 99999-9999");
        registerRequest.setPassword("12345678");
        registerRequest.setConfirmPassword("12345678");
        registerRequest.setBirthDate(LocalDate.of(1995, 8, 20));
        registerRequest.setGender("Feminino");
        registerRequest.setCountry("Brasil");

        loginRequest = new LoginRequest();
        loginRequest.setEmail("ana@email.com");
        loginRequest.setPassword("12345678");

        user = new User();
        user.setId(1L);
        user.setFirstName("Ana");
        user.setLastName("Silva");
        user.setEmail("ana@email.com");
        user.setPhone("+55 11 99999-9999");
        user.setPassword("encodedPassword");
        user.setBirthDate(LocalDate.of(1995, 8, 20));
        user.setGender("Feminino");
        user.setCountry("Brasil");
        user.setCreatedAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("Deve registrar usuário com sucesso")
    void register_Success() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserResponse response = authService.register(registerRequest);

        assertNotNull(response);
        assertEquals("Ana", response.getFirstName());
        assertEquals("ana@email.com", response.getEmail());
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando senhas não conferem")
    void register_PasswordMismatch() {
        registerRequest.setConfirmPassword("senhadiferente");

        ApiException exception = assertThrows(ApiException.class,
                () -> authService.register(registerRequest));

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, exception.getStatus());
        assertEquals("Senha e confirmar senha não conferem.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando e-mail já cadastrado")
    void register_DuplicateEmail() {
        when(userRepository.existsByEmail("ana@email.com")).thenReturn(true);

        ApiException exception = assertThrows(ApiException.class,
                () -> authService.register(registerRequest));

        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
        assertEquals("E-mail já cadastrado.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve fazer login com sucesso")
    void login_Success() {
        when(userRepository.findByEmail("ana@email.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("12345678", "encodedPassword")).thenReturn(true);
        when(jwtUtil.generateToken(1L, "ana@email.com")).thenReturn("jwt_token");

        LoginResponse response = authService.login(loginRequest);

        assertNotNull(response);
        assertEquals("jwt_token", response.getToken());
        assertEquals(1L, response.getId());
        assertEquals("Ana", response.getFirstName());
    }

    @Test
    @DisplayName("Deve lançar exceção quando usuário não encontrado no login")
    void login_UserNotFound() {
        when(userRepository.findByEmail("ana@email.com")).thenReturn(Optional.empty());

        ApiException exception = assertThrows(ApiException.class,
                () -> authService.login(loginRequest));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("Usuário não encontrado.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando senha incorreta no login")
    void login_WrongPassword() {
        when(userRepository.findByEmail("ana@email.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("12345678", "encodedPassword")).thenReturn(false);

        ApiException exception = assertThrows(ApiException.class,
                () -> authService.login(loginRequest));

        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatus());
        assertEquals("Password incorreto.", exception.getMessage());
    }
}
