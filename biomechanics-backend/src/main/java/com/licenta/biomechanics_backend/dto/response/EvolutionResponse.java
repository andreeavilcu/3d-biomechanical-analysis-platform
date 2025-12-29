package com.licenta.biomechanics_backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EvolutionResponse {
    private Long userId;
    private String userName;
    private LocalDateTime firstAssessment;
    private LocalDateTime latestAssessment;
    private Integer totalAssessments;
    private Double overallImprovement;
    private String trend;
    private List<DataPoint> scoreHistory;
    private List<MetricEvolution> metricEvolutions;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DataPoint {
        private LocalDateTime timestamp;
        private Double score;
        private String riskLevel;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MetricEvolution {
        private String metricName;
        private Double initialValue;
        private Double currentValue;
        private Double changePercentage;
        private String trend;
    }
}
