package com.saudecardiaca.service;

import com.saudecardiaca.dto.request.LoginRequest;
import com.saudecardiaca.dto.request.RegisterRequest;
import com.saudecardiaca.dto.response.LoginResponse;
import com.saudecardiaca.dto.response.UserResponse;
import com.saudecardiaca.exception.ApiException;
import com.saudecardiaca.model.User;
import com.saudecardiaca.repository.UserRepository;
import com.saudecardiaca.security.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public UserResponse register(RegisterRequest request) {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new ApiException(HttpStatus.UNPROCESSABLE_ENTITY, "Senha e confirmar senha não conferem.");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ApiException(HttpStatus.CONFLICT, "E-mail já cadastrado.");
        }

        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setBirthDate(request.getBirthDate());
        user.setGender(request.getGender());
        user.setCountry(request.getCountry());

        User saved = userRepository.save(user);
        return UserResponse.fromEntity(saved);
    }

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Usuário não encontrado."));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Password incorreto.");
        }

        String token = jwtUtil.generateToken(user.getId(), user.getEmail());

        return new LoginResponse(token, user.getId(), user.getEmail(), user.getFirstName(), user.getLastName());
    }
}
