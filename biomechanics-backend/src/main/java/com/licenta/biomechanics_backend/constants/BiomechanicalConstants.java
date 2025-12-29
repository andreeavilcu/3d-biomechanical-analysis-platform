package com.licenta.biomechanics_backend.constants;

public final class BiomechanicalConstants {

    private BiomechanicalConstants() {}

    // ========== Q ANGLE THRESHOLDS ==========
    public static final double Q_ANGLE_MALE_MIN = 10.0;
    public static final double Q_ANGLE_MALE_MAX = 14.0;
    public static final double Q_ANGLE_FEMALE_MIN = 15.0;
    public static final double Q_ANGLE_FEMALE_MAX = 17.0;
    public static final double Q_ANGLE_PATHOLOGICAL = 20.0;

    // ========== FORWARD HEAD POSTURE ==========
    public static final double FHP_MAX_STANDARD = 5.0;
    public static final double FHP_MAX_ELDERLY = 6.0;
    public static final double FHP_WARNING_THRESHOLD = 10.0;
    public static final double FHP_CRITICAL_THRESHOLD = 20.0;

    // ========== SHOULDER SYMMETRY ==========
    public static final double SHOULDER_SYMMETRY_MAX = 0.015;
    public static final double SHOULDER_ASYMMETRY_WARNING = 0.020;
    public static final double SHOULDER_ASYMMETRY_CRITICAL = 0.030;

    // ========== GAIT ANALYSIS - STANCE PHASE ==========
    public static final double STANCE_PHASE_MIN = 57.0;
    public static final double STANCE_PHASE_MAX = 63.0;
    public static final double STANCE_PHASE_IDEAL = 60.0;

    // ========== GAIT ANALYSIS - SWING PHASE ==========
    public static final double SWING_PHASE_MIN = 37.0;
    public static final double SWING_PHASE_MAX = 43.0;
    public static final double SWING_PHASE_IDEAL = 40.0;

    // ========== KNEE FLEXION ==========
    public static final double KNEE_FLEXION_MIN_STANDARD = 60.0;
    public static final double KNEE_FLEXION_MIN_ELDERLY = 55.0;
    public static final double KNEE_FLEXION_MAX = 70.0;
    public static final double KNEE_FLEXION_IDEAL = 65.0;

    // ========== CADENCE ==========
    public static final double CADENCE_MIN_STANDARD = 100.0;
    public static final double CADENCE_MIN_ELDERLY = 90.0;
    public static final double CADENCE_MAX = 120.0;
    public static final double CADENCE_FALL_RISK_THRESHOLD = 90.0;

    // ========== AGE THRESHOLDS ==========
    public static final int AGE_ELDERLY_THRESHOLD = 60;
    public static final int AGE_YOUNG_ADULT = 18;
    public static final int AGE_MIDDLE_AGED = 40;

    // ========== AGE ADJUSTMENT FACTORS ==========
    public static final double AGE_ROM_ADJUSTMENT_FACTOR = 0.85;
    public static final double AGE_STRENGTH_DECLINE_PER_DECADE = 0.12;
    public static final double AGE_TOLERANCE_INCREASE = 1.15;

    // ========== SCORING WEIGHTS ==========
    public static final double WEIGHT_CERVICAL_FHP = 3.0;
    public static final double WEIGHT_Q_ANGLE = 2.0;
    public static final double WEIGHT_SHOULDER = 1.0;

    // ========== RISK LEVELS ==========
    public static final double RISK_OPTIMAL_MAX = 20.0;
    public static final double RISK_MODERATE_MAX = 50.0;
    public static final double RISK_ELEVATED_MIN = 50.01;

    // ========== PENALTY CALCULATION ==========
    public static final double PENALTY_MAX_VALUE = 100.0;
    public static final double PENALTY_TOLERANCE_MARGIN = 0.10;
    public static final double PENALTY_MULTIPLIER = 10.0;

    // ========== FILE CONSTRAINTS ==========
    public static final long MAX_FILE_SIZE_BYTES = 50 * 1024 * 1024; // 50 MB
    public static final String[] ALLOWED_FILE_EXTENSIONS = {".ply", ".pcd"};

    // ========== POINT CLOUD PROCESSING ==========
    public static final double VOXEL_DOWNSAMPLE_SIZE = 0.015;
    public static final double DBSCAN_EPS = 0.3;
    public static final int DBSCAN_MIN_POINTS = 30;
    public static final double HUMAN_HEIGHT_MIN = 0.5;
    public static final double HUMAN_HEIGHT_MAX = 2.5;
    public static final double HUMAN_WIDTH_MAX = 1.2;

    // ========== EVOLUTION THRESHOLDS ==========
    public static final double EVOLUTION_SIGNIFICANT_IMPROVEMENT = 10.0;
    public static final double EVOLUTION_SIGNIFICANT_DECLINE = -10.0;
    public static final double EVOLUTION_STABLE_RANGE = 5.0;

    // ========== RECOMMENDATION MESSAGES ==========
    public static final String RECOMMENDATION_FHP = "Exercise: Chin Tucks (Cervical Retraction) for activating deep flexors";
    public static final String RECOMMENDATION_FHP_ERGONOMIC = "Ergonomics: Adjust monitor to eye level to reduce neck flexion";
    public static final String RECOMMENDATION_Q_ANGLE = "Exercise: Quadriceps Isometric Contraction and gluteus medius strengthening";
    public static final String RECOMMENDATION_Q_ANGLE_WARNING = "Warning: Avoid deep squats and running on hard surfaces until correction";
    public static final String RECOMMENDATION_SHOULDER = "Recommendation: Check backpack/bag carrying habits. Bilateral stretching exercises";
    public static final String RECOMMENDATION_GAIT_ASYMMETRY = "Alert: Gait asymmetry detected. Possible antalgic gait (pain-avoidance pattern)";
    public static final String RECOMMENDATION_KNEE_FLEXION = "Risk: Insufficient flexion. Increased tripping risk";
    public static final String RECOMMENDATION_CADENCE_LOW = "Alert: Low cadence. Balance exercises recommended";

    // ========== RISK LEVEL DESCRIPTIONS ==========
    public static final String RISK_OPTIMAL_DESC = "Biomechanical parameters are within normal physiological limits";
    public static final String RISK_MODERATE_DESC = "Functional postural deviations detected. Corrective exercises recommended";
    public static final String RISK_ELEVATED_DESC = "High biomechanical risk. Specialist consultation is recommended";
}
