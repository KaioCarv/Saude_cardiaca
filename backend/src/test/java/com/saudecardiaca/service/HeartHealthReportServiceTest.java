package com.saudecardiaca.service;

import com.saudecardiaca.dto.response.HeartHealthReportResponse;
import com.saudecardiaca.exception.ApiException;
import com.saudecardiaca.model.HeartHealthRecord;
import com.saudecardiaca.repository.HeartHealthRecordRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HeartHealthReportServiceTest {

    @Mock
    private HeartHealthRecordRepository recordRepository;

    @InjectMocks
    private HeartHealthReportService reportService;

    private HeartHealthRecord createRecord(int systolic, int diastolic, int heartRate,
                                            double oxygen, Double weight, List<String> symptoms) {
        HeartHealthRecord record = new HeartHealthRecord();
        record.setId(1L);
        record.setUserId(1L);
        record.setBloodPressureSystolic(systolic);
        record.setBloodPressureDiastolic(diastolic);
        record.setHeartRate(heartRate);
        record.setOxygenSaturation(oxygen);
        record.setBodyWeight(weight);
        record.setSymptoms(symptoms);
        record.setRecordedAt(LocalDateTime.now());
        record.setCreatedAt(LocalDateTime.now());
        return record;
    }

    @Test
    @DisplayName("Deve gerar relatório com médias corretas")
    void generateReport_Success() {
        HeartHealthRecord r1 = createRecord(120, 80, 72, 98.0, 68.0, List.of("falta de ar"));
        HeartHealthRecord r2 = createRecord(130, 85, 78, 97.0, 69.0, List.of("tontura"));

        when(recordRepository.findByUserIdAndRecordedAtBetweenOrderByRecordedAtDesc(any(), any(), any()))
                .thenReturn(Arrays.asList(r1, r2));

        HeartHealthReportResponse report = reportService.generateReport(
                1L, LocalDate.of(2026, 4, 1), LocalDate.of(2026, 4, 30));

        assertNotNull(report);
        assertEquals(125, report.getAverageBloodPressure().getSystolic());
        assertEquals(83, report.getAverageBloodPressure().getDiastolic());
        assertEquals(75.0, report.getAverageHeartRate());
        assertEquals(97.5, report.getAverageOxygenSaturation());
        assertEquals(1.0, report.getWeightVariation());
        assertEquals(1, report.getSymptomOccurrences().get("falta de ar"));
        assertEquals(1, report.getSymptomOccurrences().get("tontura"));
    }

    @Test
    @DisplayName("Deve classificar risco como baixo para sinais normais")
    void generateReport_LowRisk() {
        HeartHealthRecord r1 = createRecord(110, 70, 68, 99.0, 65.0, Collections.emptyList());

        when(recordRepository.findByUserIdAndRecordedAtBetweenOrderByRecordedAtDesc(any(), any(), any()))
                .thenReturn(List.of(r1));

        HeartHealthReportResponse report = reportService.generateReport(1L, LocalDate.now().minusDays(7), LocalDate.now());

        assertEquals("baixo", report.getRiskLevel());
    }

    @Test
    @DisplayName("Deve classificar risco como alto para pressão elevada com sintomas")
    void generateReport_HighRisk() {
        HeartHealthRecord r1 = createRecord(160, 100, 105, 93.0, 90.0,
                List.of("dor no peito", "falta de ar", "tontura"));

        when(recordRepository.findByUserIdAndRecordedAtBetweenOrderByRecordedAtDesc(any(), any(), any()))
                .thenReturn(List.of(r1));

        HeartHealthReportResponse report = reportService.generateReport(1L, LocalDate.now().minusDays(7), LocalDate.now());

        assertTrue(List.of("alto", "crítico").contains(report.getRiskLevel()));
    }

    @Test
    @DisplayName("Deve lançar exceção quando sem dados para relatório")
    void generateReport_NoData() {
        when(recordRepository.findByUserIdAndRecordedAtBetweenOrderByRecordedAtDesc(any(), any(), any()))
                .thenReturn(Collections.emptyList());

        ApiException exception = assertThrows(ApiException.class,
                () -> reportService.generateReport(1L, LocalDate.now().minusDays(7), LocalDate.now()));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }
}
