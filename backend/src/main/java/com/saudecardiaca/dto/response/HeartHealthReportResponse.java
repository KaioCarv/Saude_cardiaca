package com.saudecardiaca.dto.response;

import java.time.LocalDate;
import java.util.Map;

public class HeartHealthReportResponse {

    private Period period;
    private AverageBloodPressure averageBloodPressure;
    private Double averageHeartRate;
    private Double averageOxygenSaturation;
    private Double weightVariation;
    private Map<String, Integer> symptomOccurrences;
    private String riskLevel;

    public static class Period {
        private LocalDate startDate;
        private LocalDate endDate;

        public Period() {}
        public Period(LocalDate startDate, LocalDate endDate) {
            this.startDate = startDate;
            this.endDate = endDate;
        }

        public LocalDate getStartDate() { return startDate; }
        public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
        public LocalDate getEndDate() { return endDate; }
        public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    }

    public static class AverageBloodPressure {
        private Integer systolic;
        private Integer diastolic;

        public AverageBloodPressure() {}
        public AverageBloodPressure(Integer systolic, Integer diastolic) {
            this.systolic = systolic;
            this.diastolic = diastolic;
        }

        public Integer getSystolic() { return systolic; }
        public void setSystolic(Integer systolic) { this.systolic = systolic; }
        public Integer getDiastolic() { return diastolic; }
        public void setDiastolic(Integer diastolic) { this.diastolic = diastolic; }
    }

    public Period getPeriod() { return period; }
    public void setPeriod(Period period) { this.period = period; }

    public AverageBloodPressure getAverageBloodPressure() { return averageBloodPressure; }
    public void setAverageBloodPressure(AverageBloodPressure averageBloodPressure) { this.averageBloodPressure = averageBloodPressure; }

    public Double getAverageHeartRate() { return averageHeartRate; }
    public void setAverageHeartRate(Double averageHeartRate) { this.averageHeartRate = averageHeartRate; }

    public Double getAverageOxygenSaturation() { return averageOxygenSaturation; }
    public void setAverageOxygenSaturation(Double averageOxygenSaturation) { this.averageOxygenSaturation = averageOxygenSaturation; }

    public Double getWeightVariation() { return weightVariation; }
    public void setWeightVariation(Double weightVariation) { this.weightVariation = weightVariation; }

    public Map<String, Integer> getSymptomOccurrences() { return symptomOccurrences; }
    public void setSymptomOccurrences(Map<String, Integer> symptomOccurrences) { this.symptomOccurrences = symptomOccurrences; }

    public String getRiskLevel() { return riskLevel; }
    public void setRiskLevel(String riskLevel) { this.riskLevel = riskLevel; }
}
