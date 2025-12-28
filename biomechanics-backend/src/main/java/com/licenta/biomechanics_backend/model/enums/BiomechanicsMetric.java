package com.licenta.biomechanics_backend.model.enums;

import lombok.Getter;

@Getter
public enum BiomechanicsMetric {

    Q_ANGLE(
            "Q Angle (Quadriceps Angle)",
            "Degrees (°)",
            Category.STATIC,
            "The angle formed between the femur vector and the tibia vector. High values indicate a risk of genu valgum."
    ),
    FORWARD_HEAD_POSTURE(
            "Head Alignment (FHP)",
            "Degrees (°)",
            Category.STATIC,
            "Anterior deviation of the head relative to the vertical line of the shoulders."
    ),
    SHOULDER_SYMMETRY(
            "Shoulder Symmetry",
            "Meters (m)",
            Category.STATIC,
            "Vertical (Z-axis) level difference between the two shoulders."
    ),
    GAIT_STANCE_PHASE(
            "Stance Phase",
            "Percentage (%)",
            Category.DYNAMIC,
            "Percentage of the gait cycle during which the foot is in contact with the ground."
    ),
    KNEE_FLEXION_SWING(
            "Max Knee Flexion (Swing)",
            "Degrees (°)",
            Category.DYNAMIC,
            "Maximum flexion angle reached during the swing phase."
    ),
    CADENCE(
            "Cadence",
            "Steps/min",
            Category.DYNAMIC,
            "Number of steps performed per minute."
    );

    private final String displayName;
    private final String unit;
    private final Category category;
    private final String description;

    BiomechanicsMetric(String displayName, String unit, Category category, String description) {
        this.displayName = displayName;
        this.unit = unit;
        this.category = category;
        this.description = description;
    }

    public enum Category {
        STATIC,
        DYNAMIC
    }
}
