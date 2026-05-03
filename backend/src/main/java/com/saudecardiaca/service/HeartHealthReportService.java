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
    private final GeminiService geminiService;

    public HeartHealthReportService(HeartHealthRecordRepository recordRepository,
                                    GeminiService geminiService) {
        this.recordRepository = recordRepository;
        this.geminiService = geminiService;
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

        report.setAiInsights(geminiService.generateInsights(buildPrompt(report, records.size())));

        return report;
    }

    
    private String buildPrompt(HeartHealthReportResponse report, int recordCount) {
        StringBuilder symptoms = new StringBuilder();
        if (report.getSymptomOccurrences() != null && !report.getSymptomOccurrences().isEmpty()) {
            report.getSymptomOccurrences().forEach((s, c) ->
                    symptoms.append("- ").append(s).append(": ").append(c).append(" vez(es)\n"));
        } else {
            symptoms.append("Nenhum sintoma registrado.\n");
        }

        return """
                Analise estes dados agregados de monitoramento cardíaco e gere insights concretos em português.

                DADOS (período %s a %s, %d registros):
                - Pressão arterial média: %d/%d mmHg
                - Frequência cardíaca média: %.1f bpm
                - Saturação de oxigênio média: %.1f%%
                - Variação de peso: %.1f kg
                - Nível de risco: %s
                - Sintomas:
                %s

                REGRAS OBRIGATÓRIAS:
                - NÃO comece com saudação ("Olá", "Oi") nem diga que "analisou os dados".
                - Comece direto pela análise dos valores.
                - Cite os números reais ao comentar (ex: "A pressão média de 120/80 mmHg está...").
                - Para cada métrica, diga se está dentro da faixa de referência adulto saudável e o que significa.
                - Não invente valores nem dê diagnóstico clínico.

                FORMATO (use exatamente estas seções, com **negrito**):

                **Análise dos sinais vitais:** (3-5 frases comentando cada métrica com os valores reais)

                **Pontos de atenção:** (lista os números fora da faixa ideal; se tudo normal, escreva "Sem alterações relevantes nos dados.")

                **Sugestões de hábitos:** (3 sugestões objetivas e específicas, ligadas aos achados)

                **Quando procurar um médico:** (1-2 frases sobre sinais que pedem avaliação)

                Encerre com a frase: "Este resumo é informativo e não substitui consulta médica."
                """.formatted(
                report.getPeriod().getStartDate(),
                report.getPeriod().getEndDate(),
                recordCount,
                report.getAverageBloodPressure().getSystolic(),
                report.getAverageBloodPressure().getDiastolic(),
                report.getAverageHeartRate(),
                report.getAverageOxygenSaturation(),
                report.getWeightVariation(),
                report.getRiskLevel(),
                symptoms.toString()
        );
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
