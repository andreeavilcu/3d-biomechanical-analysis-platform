package com.licenta.biomechanics_backend.model;

import com.licenta.biomechanics_backend.model.enums.BiomechanicsMetric;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "metric_results")
public class MetricResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "assessment_id", nullable = false)
    private Assessment assessment;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BiomechanicsMetric metricType;

    private Double measuredValue;
    private Double penaltyScore;
    private String statusLabel;
    private String recommendation;
}
