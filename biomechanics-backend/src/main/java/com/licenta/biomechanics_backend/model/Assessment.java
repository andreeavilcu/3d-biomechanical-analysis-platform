package com.licenta.biomechanics_backend.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Table(name = "assessments")
public class Assessment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private LocalDateTime createdAt;
    private Double globalPostureScore;
    private String riskLevel;

    @OneToMany(mappedBy = "assessment", cascade = CascadeType.ALL)
    private List<MetricResult> metrics;
}
