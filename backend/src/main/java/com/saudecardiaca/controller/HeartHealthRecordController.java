package com.saudecardiaca.controller;

import com.saudecardiaca.dto.request.HeartHealthRecordRequest;
import com.saudecardiaca.dto.response.HeartHealthRecordListResponse;
import com.saudecardiaca.dto.response.HeartHealthRecordResponse;
import com.saudecardiaca.service.HeartHealthRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/heart-health-records")
@Tag(name = "Registros Cardíacos", description = "Registro e consulta de acompanhamento de saúde cardíaca")
@SecurityRequirement(name = "bearerAuth")
public class HeartHealthRecordController {

    private final HeartHealthRecordService recordService;

    public HeartHealthRecordController(HeartHealthRecordService recordService) {
        this.recordService = recordService;
    }

    @PostMapping
    @Operation(summary = "Registrar acompanhamento de saúde cardíaca")
    public ResponseEntity<HeartHealthRecordResponse> create(
            Authentication authentication,
            @Valid @RequestBody HeartHealthRecordRequest request) {
        Long userId = (Long) authentication.getPrincipal();
        HeartHealthRecordResponse response = recordService.create(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Consultar histórico de saúde cardíaca")
    public ResponseEntity<HeartHealthRecordListResponse> findAll(
            Authentication authentication,
            @RequestParam(required = false) @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate endDate,
            @RequestParam(required = false, defaultValue = "20") Integer limit) {
        Long userId = (Long) authentication.getPrincipal();
        HeartHealthRecordListResponse response = recordService.findByUser(userId, startDate, endDate, limit);
        return ResponseEntity.ok(response);
    }
}
