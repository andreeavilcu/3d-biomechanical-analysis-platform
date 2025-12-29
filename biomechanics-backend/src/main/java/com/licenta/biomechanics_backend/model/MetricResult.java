package com.licenta.biomechanics_backend.model;

import com.licenta.biomechanics_backend.model.enums.BiomechanicsMetric;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;

@Entity
@Data
@Table(name = "metric_results")
public class MetricResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assessment_id", nullable = false)
    @NotNull(message = "Assessment is required")
    private Assessment assessment;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    @NotNull(message = "Metric type is required")
    private BiomechanicsMetric metricType;

    @NotNull(message = "Measured value is required")
    @Column(nullable = false)
    private Double measuredValue;


    @NotNull(message = "Penalty score is required")
    @DecimalMin(value = "0.0", message = "Penalty must be >= 0")
    @DecimalMax(value = "100.0", message = "Penalty must be <= 100")
    @Column(nullable = false)
    private Double penaltyScore;


    @NotBlank(message = "Status label is required")
    @Column(nullable = false, length = 20)
    private String statusLabel;

    @Column(length = 500)
    private String recommendation;
}
