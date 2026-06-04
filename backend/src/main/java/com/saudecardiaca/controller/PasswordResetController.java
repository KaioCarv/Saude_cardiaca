package com.saudecardiaca.controller;

import com.saudecardiaca.dto.request.ForgotPasswordRequest;
import com.saudecardiaca.dto.request.ResetPasswordRequest;
import com.saudecardiaca.service.PasswordResetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@Tag(name = "Redefinição de Senha", description = "Fluxo de esqueci a senha via e-mail")
public class PasswordResetController {

    private final PasswordResetService passwordResetService;

    public PasswordResetController(PasswordResetService passwordResetService) {
        this.passwordResetService = passwordResetService;
    }

    @PostMapping("/forgot-password")
    @Operation(summary = "Solicitar código", description = "Envia um código de 6 dígitos para o e-mail cadastrado.")
    public ResponseEntity<Map<String, String>> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        passwordResetService.sendResetCode(request);
        return ResponseEntity.ok(Map.of("mensagem", "Código enviado para o e-mail informado."));
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Redefinir senha", description = "Valida o código e altera a senha do usuário.")
    public ResponseEntity<Map<String, String>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        passwordResetService.resetPassword(request);
        return ResponseEntity.ok(Map.of("mensagem", "Senha redefinida com sucesso."));
    }
}
