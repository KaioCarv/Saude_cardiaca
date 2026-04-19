package com.saudecardiaca.exception;

import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.saudecardiaca.dto.response.ErrorResponse;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErrorResponse> handleApiException(ApiException ex) {
        ErrorResponse error = new ErrorResponse(ex.getStatus().value(), ex.getMessage());
        return ResponseEntity.status(ex.getStatus()).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex, HttpServletRequest request) {

        String path = request.getRequestURI();
        String detailedMessage = buildValidationMessage(ex, path);

        ErrorResponse error = new ErrorResponse(400, detailedMessage);
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleMessageNotReadable(
            HttpMessageNotReadableException ex, HttpServletRequest request) {

        String path = request.getRequestURI();
        String message;

        String exMsg = ex.getMessage() != null ? ex.getMessage() : "";

        if (exMsg.contains("LocalDateTime")) {
            message = "Formato de data/hora inválido. Use o formato dd/MM/yyyy HH:mm:ss.";
        } else if (exMsg.contains("LocalDate")) {
            message = "Formato de data inválido. Use o formato dd/MM/yyyy.";
        } else {
            message = getContextMessage(path) + " Verifique o corpo da requisição.";
        }

        ErrorResponse error = new ErrorResponse(400, message);
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String paramName = ex.getName();
        String message;

        if ("startDate".equals(paramName) || "endDate".equals(paramName)) {
            message = "Parâmetro '" + paramName + "' com formato inválido. Use o formato dd/MM/yyyy.";
        } else if ("limit".equals(paramName)) {
            message = "Parâmetro 'limit' deve ser um número inteiro válido.";
        } else {
            message = "Parâmetro '" + paramName + "' com valor inválido.";
        }

        ErrorResponse error = new ErrorResponse(400, message);
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParam(MissingServletRequestParameterException ex) {
        String message = "Parâmetro obrigatório '" + ex.getParameterName() + "' não informado.";
        ErrorResponse error = new ErrorResponse(400, message);
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        ErrorResponse error = new ErrorResponse(500, "Erro no servidor.");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    private String buildValidationMessage(MethodArgumentNotValidException ex, String path) {
        String fields = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .collect(Collectors.joining("; "));

        String context = getContextMessage(path);
        return context + " Campos inválidos: [" + fields + "].";
    }

    private String getContextMessage(String path) {
        if (path.contains("/register")) {
            return "Dados do cadastro enviados incorretamente.";
        } else if (path.contains("/login")) {
            return "Dados do request enviados incorretos.";
        } else if (path.contains("/heart-health-records")) {
            return "Dados de acompanhamento enviados incorretamente.";
        } else if (path.contains("/heart-health-reports")) {
            return "Parâmetros do relatório inválidos.";
        }
        return "Dados enviados incorretamente.";
    }
}
