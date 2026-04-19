package com.saudecardiaca.dto.response;

import com.saudecardiaca.model.HeartHealthRecord;
import java.time.LocalDateTime;
import java.util.List;

public class HeartHealthRecordResponse {

    private Long id;
    private Long userId;
    private Integer bloodPressureSystolic;
    private Integer bloodPressureDiastolic;
    private Integer heartRate;
    private Double oxygenSaturation;
    private Double bodyWeight;
    private List<String> symptoms;
    private LocalDateTime recordedAt;
    private LocalDateTime createdAt;

    public static HeartHealthRecordResponse fromEntity(HeartHealthRecord record) {
        HeartHealthRecordResponse response = new HeartHealthRecordResponse();
        response.setId(record.getId());
        response.setUserId(record.getUserId());
        response.setBloodPressureSystolic(record.getBloodPressureSystolic());
        response.setBloodPressureDiastolic(record.getBloodPressureDiastolic());
        response.setHeartRate(record.getHeartRate());
        response.setOxygenSaturation(record.getOxygenSaturation());
        response.setBodyWeight(record.getBodyWeight());
        response.setSymptoms(record.getSymptoms());
        response.setRecordedAt(record.getRecordedAt());
        response.setCreatedAt(record.getCreatedAt());
        return response;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

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

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
