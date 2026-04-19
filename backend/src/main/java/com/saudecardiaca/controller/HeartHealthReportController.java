package com.saudecardiaca.controller;

import com.saudecardiaca.dto.response.HeartHealthReportResponse;
import com.saudecardiaca.service.HeartHealthReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/heart-health-reports")
@Tag(name = "Relatórios", description = "Geração de relatórios analíticos de saúde cardíaca")
@SecurityRequirement(name = "bearerAuth")
public class HeartHealthReportController {

    private final HeartHealthReportService reportService;

    public HeartHealthReportController(HeartHealthReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping
    @Operation(summary = "Gerar relatório de saúde cardíaca",
               description = "Gera um relatório analítico consolidado de saúde cardíaca do usuário autenticado.")
    public ResponseEntity<HeartHealthReportResponse> generateReport(
            Authentication authentication,
            @RequestParam(required = false) @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate endDate,
            @RequestParam(required = false) String groupBy) {
        Long userId = (Long) authentication.getPrincipal();
        HeartHealthReportResponse response = reportService.generateReport(userId, startDate, endDate);
        return ResponseEntity.ok(response);
    }
}
