package com.licenta.biomechanics_backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "assessments")
public class Assessment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @NotNull(message = "User is required")
    private User user;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @NotNull(message = "Global Posture Score is required")
    @DecimalMin(value = "0.0", message = "GPS Score must be >= 0")
    @DecimalMax(value = "100.0", message = "GPS Score must be <= 100")
    @Column(nullable = false)
    private Double globalPostureScore;


    @NotBlank(message = "Risk level is required")
    @Pattern(regexp = "OPTIMAL|MODERATE|ELEVATED", message = "Invalid risk level")
    @Column(nullable = false, length = 20)
    private String riskLevel;

    @OneToMany(mappedBy = "assessment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MetricResult> metrics = new ArrayList<>();

    private String notes;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }


    public void addMetric(MetricResult metric) {
        metrics.add(metric);
        metric.setAssessment(this);
    }


    public void removeMetric(MetricResult metric) {
        metrics.remove(metric);
        metric.setAssessment(null);
    }
}
