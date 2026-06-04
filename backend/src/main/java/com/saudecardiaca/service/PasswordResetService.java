package com.saudecardiaca.service;

import com.saudecardiaca.dto.request.ForgotPasswordRequest;
import com.saudecardiaca.dto.request.ResetPasswordRequest;
import com.saudecardiaca.exception.ApiException;
import com.saudecardiaca.model.PasswordResetToken;
import com.saudecardiaca.model.User;
import com.saudecardiaca.repository.PasswordResetTokenRepository;
import com.saudecardiaca.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
public class PasswordResetService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Value("${password.reset.code.expiry-minutes:15}")
    private int expiryMinutes;

    public PasswordResetService(UserRepository userRepository,
                                PasswordResetTokenRepository tokenRepository,
                                PasswordEncoder passwordEncoder,
                                EmailService emailService) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    @Transactional
    public void sendResetCode(ForgotPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "E-mail não cadastrado."));

        tokenRepository.deleteAllByEmail(user.getEmail());

        String code = generateCode();
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(expiryMinutes);
        tokenRepository.save(new PasswordResetToken(user.getEmail(), code, expiresAt));

        emailService.sendPasswordResetCode(user.getEmail(), user.getFirstName(), code);
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new ApiException(HttpStatus.UNPROCESSABLE_ENTITY, "Senha e confirmar senha não conferem.");
        }

        PasswordResetToken token = tokenRepository
                .findTopByEmailAndUsedFalseOrderByExpiresAtDesc(request.getEmail())
                .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "Código inválido ou expirado."));

        if (LocalDateTime.now().isAfter(token.getExpiresAt())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Código expirado. Solicite um novo.");
        }

        if (!token.getCode().equals(request.getCode())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Código incorreto.");
        }

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Usuário não encontrado."));

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        token.setUsed(true);
        tokenRepository.save(token);
    }

    private String generateCode() {
        SecureRandom random = new SecureRandom();
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }
}
