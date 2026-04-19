package com.saudecardiaca.service;

import com.saudecardiaca.dto.request.HeartHealthRecordRequest;
import com.saudecardiaca.dto.response.HeartHealthRecordListResponse;
import com.saudecardiaca.dto.response.HeartHealthRecordResponse;
import com.saudecardiaca.exception.ApiException;
import com.saudecardiaca.model.HeartHealthRecord;
import com.saudecardiaca.repository.HeartHealthRecordRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class HeartHealthRecordService {

    private final HeartHealthRecordRepository recordRepository;

    public HeartHealthRecordService(HeartHealthRecordRepository recordRepository) {
        this.recordRepository = recordRepository;
    }

    public HeartHealthRecordResponse create(Long userId, HeartHealthRecordRequest request) {
        HeartHealthRecord record = new HeartHealthRecord();
        record.setUserId(userId);
        record.setBloodPressureSystolic(request.getBloodPressureSystolic());
        record.setBloodPressureDiastolic(request.getBloodPressureDiastolic());
        record.setHeartRate(request.getHeartRate());
        record.setOxygenSaturation(request.getOxygenSaturation());
        record.setBodyWeight(request.getBodyWeight());
        record.setSymptoms(request.getSymptoms());
        record.setRecordedAt(request.getRecordedAt());

        HeartHealthRecord saved = recordRepository.save(record);
        return HeartHealthRecordResponse.fromEntity(saved);
    }

    public HeartHealthRecordListResponse findByUser(Long userId, LocalDate startDate, LocalDate endDate, Integer limit) {
        List<HeartHealthRecord> records;

        if (startDate != null && endDate != null) {
            LocalDateTime start = startDate.atStartOfDay();
            LocalDateTime end = endDate.atTime(LocalTime.MAX);
            records = recordRepository.findByUserIdAndRecordedAtBetweenOrderByRecordedAtDesc(userId, start, end);
        } else if (startDate != null) {
            LocalDateTime start = startDate.atStartOfDay();
            records = recordRepository.findByUserIdAndRecordedAtGreaterThanEqualOrderByRecordedAtDesc(userId, start);
        } else if (endDate != null) {
            LocalDateTime end = endDate.atTime(LocalTime.MAX);
            records = recordRepository.findByUserIdAndRecordedAtLessThanEqualOrderByRecordedAtDesc(userId, end);
        } else {
            records = recordRepository.findByUserIdOrderByRecordedAtDesc(userId);
        }

        if (records.isEmpty()) {
            throw new ApiException(HttpStatus.NOT_FOUND, "Nenhum registro encontrado.");
        }

        int effectiveLimit = (limit != null && limit > 0) ? limit : 20;
        List<HeartHealthRecordResponse> items = records.stream()
                .limit(effectiveLimit)
                .map(HeartHealthRecordResponse::fromEntity)
                .collect(Collectors.toList());

        return new HeartHealthRecordListResponse(items);
    }
}
