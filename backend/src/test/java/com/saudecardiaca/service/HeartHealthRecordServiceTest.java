package com.saudecardiaca.service;

import com.saudecardiaca.dto.request.HeartHealthRecordRequest;
import com.saudecardiaca.dto.response.HeartHealthRecordListResponse;
import com.saudecardiaca.dto.response.HeartHealthRecordResponse;
import com.saudecardiaca.exception.ApiException;
import com.saudecardiaca.model.HeartHealthRecord;
import com.saudecardiaca.repository.HeartHealthRecordRepository;
import org.junit.jupiter.api.BeforeEach;
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
class HeartHealthRecordServiceTest {

    @Mock
    private HeartHealthRecordRepository recordRepository;

    @InjectMocks
    private HeartHealthRecordService recordService;

    private HeartHealthRecordRequest recordRequest;
    private HeartHealthRecord record;

    @BeforeEach
    void setUp() {
        recordRequest = new HeartHealthRecordRequest();
        recordRequest.setBloodPressureSystolic(120);
        recordRequest.setBloodPressureDiastolic(80);
        recordRequest.setHeartRate(72);
        recordRequest.setOxygenSaturation(98.0);
        recordRequest.setBodyWeight(68.5);
        recordRequest.setSymptoms(List.of("falta de ar"));
        recordRequest.setRecordedAt(LocalDateTime.of(2026, 4, 14, 9, 30));

        record = new HeartHealthRecord();
        record.setId(101L);
        record.setUserId(1L);
        record.setBloodPressureSystolic(120);
        record.setBloodPressureDiastolic(80);
        record.setHeartRate(72);
        record.setOxygenSaturation(98.0);
        record.setBodyWeight(68.5);
        record.setSymptoms(List.of("falta de ar"));
        record.setRecordedAt(LocalDateTime.of(2026, 4, 14, 9, 30));
        record.setCreatedAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("Deve criar registro cardíaco com sucesso")
    void create_Success() {
        when(recordRepository.save(any(HeartHealthRecord.class))).thenReturn(record);

        HeartHealthRecordResponse response = recordService.create(1L, recordRequest);

        assertNotNull(response);
        assertEquals(101L, response.getId());
        assertEquals(120, response.getBloodPressureSystolic());
        assertEquals(72, response.getHeartRate());
        verify(recordRepository).save(any(HeartHealthRecord.class));
    }

    @Test
    @DisplayName("Deve retornar histórico de registros")
    void findByUser_Success() {
        when(recordRepository.findByUserIdOrderByRecordedAtDesc(1L))
                .thenReturn(Arrays.asList(record));

        HeartHealthRecordListResponse response = recordService.findByUser(1L, null, null, 20);

        assertNotNull(response);
        assertEquals(1, response.getItems().size());
    }

    @Test
    @DisplayName("Deve retornar registros filtrados por período")
    void findByUser_WithDateFilter() {
        when(recordRepository.findByUserIdAndRecordedAtBetweenOrderByRecordedAtDesc(
                any(), any(), any())).thenReturn(Arrays.asList(record));

        HeartHealthRecordListResponse response = recordService.findByUser(
                1L, LocalDate.of(2026, 4, 1), LocalDate.of(2026, 4, 30), 20);

        assertNotNull(response);
        assertEquals(1, response.getItems().size());
    }

    @Test
    @DisplayName("Deve lançar exceção quando nenhum registro encontrado")
    void findByUser_NoRecords() {
        when(recordRepository.findByUserIdOrderByRecordedAtDesc(1L))
                .thenReturn(Collections.emptyList());

        ApiException exception = assertThrows(ApiException.class,
                () -> recordService.findByUser(1L, null, null, 20));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    @DisplayName("Deve respeitar limite de registros")
    void findByUser_WithLimit() {
        HeartHealthRecord record2 = new HeartHealthRecord();
        record2.setId(102L);
        record2.setUserId(1L);
        record2.setBloodPressureSystolic(130);
        record2.setBloodPressureDiastolic(85);
        record2.setHeartRate(78);
        record2.setOxygenSaturation(97.0);
        record2.setRecordedAt(LocalDateTime.of(2026, 4, 15, 9, 30));
        record2.setCreatedAt(LocalDateTime.now());

        when(recordRepository.findByUserIdOrderByRecordedAtDesc(1L))
                .thenReturn(Arrays.asList(record, record2));

        HeartHealthRecordListResponse response = recordService.findByUser(1L, null, null, 1);

        assertEquals(1, response.getItems().size());
    }
}
