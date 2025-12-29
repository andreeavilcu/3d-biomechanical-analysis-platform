package com.licenta.biomechanics_backend.dto.mapper;

import com.licenta.biomechanics_backend.dto.response.AssessmentResponse;
import com.licenta.biomechanics_backend.dto.response.MetricDetailResponse;
import com.licenta.biomechanics_backend.logic.ScoringEngine;
import com.licenta.biomechanics_backend.model.Assessment;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class AssessmentMapper {

    public AssessmentResponse toResponse(Assessment assessment, ScoringEngine.FinalReport report,
                                         Double evolutionPercentage){
        List<MetricDetailResponse> metricResponses = report.getDetails().stream()
                .map(this::toMetricResponse)
                .collect(Collectors.toList());

        long criticalCount = metricResponses.stream()
                .filter(m -> m.getPenaltyScore() > 50)
                .count();

        long warningCount = metricResponses.stream()
                .filter(m -> m.getPenaltyScore() > 0 && m.getPenaltyScore() <= 50)
                .count();

        List<String> exercises = metricResponses.stream()
                .filter(m -> m.getRecommendation() != null)
                .map(MetricDetailResponse::getRecommendation)
                .collect(Collectors.toList());

        return AssessmentResponse.builder()
                .id(assessment.getId())
                .userId(assessment.getUser().getId())
                .createdAt(assessment.getCreatedAt())
                .globalPostureScore(report.getGpsScore())
                .riskLevel(report.getRiskLevel())
                .riskLevelDescription(getRiskLevelDescription(report.getRiskLevel()))
                .evolutionPercentage(evolutionPercentage)
                .metrics(metricResponses)
                .recommendations(AssessmentResponse.RecommendationSummary.builder()
                        .criticalIssues((int) criticalCount)
                        .warnings((int) warningCount)
                        .primaryRecommendation(getPrimaryRecommendation(report.getRiskLevel()))
                        .exerciseRecommendations(exercises)
                        .build())
            .build();
    }



    private MetricDetailResponse toMetricResponse(ScoringEngine.MetricDetail detail){
        return MetricDetailResponse.builder()
                .name(detail.getName())
                .displayName(detail.getName().getDisplayName())
                .unit(detail.getName().getUnit())
                .measuredValue(detail.getValue())
                .penaltyScore(detail.getPenalty())
                .statusLabel(getStatusLabel(detail.getPenalty()))
                .recommendation(detail.getRecommendation())
                .build();
    }

    private String getStatusLabel(double penalty){
        if (penalty == 0) return "Normal";
        if (penalty <= 50) return "Warning";
        return "Critical";
    }

    private String getRiskLevelDescription(String riskLevel) {
        return switch (riskLevel) {
            case "OPTIMAL" -> "Biomechanical parameters are within normal physiological limits";
            case "MODERATE" -> "Functional postural deviations detected. Corrective exercises are recommended";
            case "ELEVATED" -> "High biomechanical risk. Consultation with a specialist is recommended";
            default -> "Unknown risk level";
        };
    }

    private String getPrimaryRecommendation(String riskLevel) {
        return switch (riskLevel) {
            case "OPTIMAL" -> "Maintain current physical activity";
            case "MODERATE" -> "Active breaks and stretching exercises are recommended";
            case "ELEVATED" -> "Consultation with a physical therapist is advised";
            default -> "Further evaluation is required";
        };
    }
}
