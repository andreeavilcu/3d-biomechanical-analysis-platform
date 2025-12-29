package com.licenta.biomechanics_backend.dto.response;

import com.licenta.biomechanics_backend.model.enums.BiomechanicsMetric;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MetricDetailResponse {
    private BiomechanicsMetric name;
    private String displayName;
    private String unit;
    private Double measuredValue;
    private Double penaltyScore;
    private String statusLabel;
    private String recommendation;
    private ReferenceRange referenceRange;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReferenceRange {
        private Double min;
        private Double max;
        private String description;
    }
}
