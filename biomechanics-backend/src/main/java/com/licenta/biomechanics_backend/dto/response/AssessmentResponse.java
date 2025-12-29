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
public class AssessmentResponse {
    private Long id;
    private Long userId;
    private LocalDateTime createdAt;
    private Double globalPostureScore;
    private String riskLevel;  // "OPTIMAL", "MODERATE", "ELEVATED"
    private String riskLevelDescription;
    private Double evolutionPercentage;
    private List<MetricDetailResponse> metrics;
    private RecommendationSummary recommendations;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecommendationSummary {
        private Integer criticalIssues;
        private Integer warnings;
        private String primaryRecommendation;
        private List<String> exerciseRecommendations;
    }
}
