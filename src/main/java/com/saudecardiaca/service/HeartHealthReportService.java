package com.saudecardiaca.service;

import com.saudecardiaca.dto.response.HeartHealthReportResponse;
import com.saudecardiaca.exception.ApiException;
import com.saudecardiaca.model.HeartHealthRecord;
import com.saudecardiaca.repository.HeartHealthRecordRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class HeartHealthReportService {

    private final HeartHealthRecordRepository recordRepository;

    public HeartHealthReportService(HeartHealthRecordRepository recordRepository) {
        this.recordRepository = recordRepository;
    }

    public HeartHealthReportResponse generateReport(Long userId, LocalDate startDate, LocalDate endDate) {
        LocalDate effectiveStart = (startDate != null) ? startDate : LocalDate.now().minusMonths(1);
        LocalDate effectiveEnd = (endDate != null) ? endDate : LocalDate.now();

        LocalDateTime start = effectiveStart.atStartOfDay();
        LocalDateTime end = effectiveEnd.atTime(LocalTime.MAX);

        List<HeartHealthRecord> records = recordRepository
                .findByUserIdAndRecordedAtBetweenOrderByRecordedAtDesc(userId, start, end);

        if (records.isEmpty()) {
            throw new ApiException(HttpStatus.NOT_FOUND, "Dados insuficientes para gerar relatório.");
        }

        HeartHealthReportResponse report = new HeartHealthReportResponse();

        report.setPeriod(new HeartHealthReportResponse.Period(effectiveStart, effectiveEnd));

        double avgSystolic = records.stream().mapToInt(HeartHealthRecord::getBloodPressureSystolic).average().orElse(0);
        double avgDiastolic = records.stream().mapToInt(HeartHealthRecord::getBloodPressureDiastolic).average().orElse(0);
        report.setAverageBloodPressure(new HeartHealthReportResponse.AverageBloodPressure(
                (int) Math.round(avgSystolic), (int) Math.round(avgDiastolic)));

        double avgHeartRate = records.stream().mapToInt(HeartHealthRecord::getHeartRate).average().orElse(0);
        report.setAverageHeartRate(Math.round(avgHeartRate * 10.0) / 10.0);

        double avgOxygen = records.stream().mapToDouble(HeartHealthRecord::getOxygenSaturation).average().orElse(0);
        report.setAverageOxygenSaturation(Math.round(avgOxygen * 10.0) / 10.0);

        List<Double> weights = records.stream()
                .map(HeartHealthRecord::getBodyWeight)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        if (weights.size() >= 2) {
            double maxWeight = Collections.max(weights);
            double minWeight = Collections.min(weights);
            report.setWeightVariation(Math.round((maxWeight - minWeight) * 10.0) / 10.0);
        } else {
            report.setWeightVariation(0.0);
        }

        Map<String, Integer> symptomMap = new LinkedHashMap<>();
        for (HeartHealthRecord record : records) {
            if (record.getSymptoms() != null) {
                for (String symptom : record.getSymptoms()) {
                    symptomMap.merge(symptom, 1, Integer::sum);
                }
            }
        }
        report.setSymptomOccurrences(symptomMap);

        report.setRiskLevel(calculateRiskLevel(avgSystolic, avgDiastolic, avgHeartRate, avgOxygen, symptomMap));

        return report;
    }

    private String calculateRiskLevel(double avgSystolic, double avgDiastolic,
                                      double avgHeartRate, double avgOxygen,
                                      Map<String, Integer> symptoms) {
        int riskScore = 0;

        if (avgSystolic >= 180 || avgDiastolic >= 120) riskScore += 3;
        else if (avgSystolic >= 140 || avgDiastolic >= 90) riskScore += 2;
        else if (avgSystolic >= 130 || avgDiastolic >= 85) riskScore += 1;

        if (avgHeartRate > 100 || avgHeartRate < 50) riskScore += 2;
        else if (avgHeartRate > 90 || avgHeartRate < 55) riskScore += 1;

        if (avgOxygen < 90) riskScore += 3;
        else if (avgOxygen < 94) riskScore += 2;
        else if (avgOxygen < 96) riskScore += 1;

        int totalSymptoms = symptoms.values().stream().mapToInt(Integer::intValue).sum();
        if (totalSymptoms >= 10) riskScore += 2;
        else if (totalSymptoms >= 5) riskScore += 1;

        if (symptoms.containsKey("dor no peito")) riskScore += 2;

        if (riskScore >= 7) return "crítico";
        if (riskScore >= 4) return "alto";
        if (riskScore >= 2) return "moderado";
        return "baixo";
    }
}
