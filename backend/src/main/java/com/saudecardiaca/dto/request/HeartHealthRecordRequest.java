package com.saudecardiaca.dto.request;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class HeartHealthRecordRequest {

    @NotNull(message = "Pressão sistólica é obrigatória")
    @Min(value = 50, message = "Pressão sistólica deve ser no mínimo 50 mmHg")
    @Max(value = 300, message = "Pressão sistólica deve ser no máximo 300 mmHg")
    private Integer bloodPressureSystolic;

    @NotNull(message = "Pressão diastólica é obrigatória")
    @Min(value = 30, message = "Pressão diastólica deve ser no mínimo 30 mmHg")
    @Max(value = 200, message = "Pressão diastólica deve ser no máximo 200 mmHg")
    private Integer bloodPressureDiastolic;

    @NotNull(message = "Frequência cardíaca é obrigatória")
    @Min(value = 20, message = "Frequência cardíaca deve ser no mínimo 20 bpm")
    @Max(value = 300, message = "Frequência cardíaca deve ser no máximo 300 bpm")
    private Integer heartRate;

    @NotNull(message = "Saturação de oxigênio é obrigatória")
    @DecimalMin(value = "50.0", message = "Saturação de oxigênio deve ser no mínimo 50%")
    @DecimalMax(value = "100.0", message = "Saturação de oxigênio deve ser no máximo 100%")
    private Double oxygenSaturation;

    @DecimalMin(value = "1.0", message = "Peso deve ser no mínimo 1 kg")
    @DecimalMax(value = "500.0", message = "Peso deve ser no máximo 500 kg")
    private Double bodyWeight;

    private List<String> symptoms;

    
    @NotNull(message = "Data/hora da medição é obrigatória")
    private LocalDateTime recordedAt;

    public Integer getBloodPressureSystolic() { return bloodPressureSystolic; }
    public void setBloodPressureSystolic(Integer bloodPressureSystolic) { this.bloodPressureSystolic = bloodPressureSystolic; }

    public Integer getBloodPressureDiastolic() { return bloodPressureDiastolic; }
    public void setBloodPressureDiastolic(Integer bloodPressureDiastolic) { this.bloodPressureDiastolic = bloodPressureDiastolic; }

    public Integer getHeartRate() { return heartRate; }
    public void setHeartRate(Integer heartRate) { this.heartRate = heartRate; }

    public Double getOxygenSaturation() { return oxygenSaturation; }
    public void setOxygenSaturation(Double oxygenSaturation) { this.oxygenSaturation = oxygenSaturation; }

    public Double getBodyWeight() { return bodyWeight; }
    public void setBodyWeight(Double bodyWeight) { this.bodyWeight = bodyWeight; }

    public List<String> getSymptoms() { return symptoms; }
    public void setSymptoms(List<String> symptoms) { this.symptoms = symptoms; }

    public LocalDateTime getRecordedAt() { return recordedAt; }
    public void setRecordedAt(LocalDateTime recordedAt) { this.recordedAt = recordedAt; }
}
