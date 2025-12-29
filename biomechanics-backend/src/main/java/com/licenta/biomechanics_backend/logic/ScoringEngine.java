package com.licenta.biomechanics_backend.logic;

import com.licenta.biomechanics_backend.constants.BiomechanicalConstants;
import com.licenta.biomechanics_backend.model.enums.BiomechanicsMetric;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Component
public class ScoringEngine {

    @Data
    public static class FinalReport{
        double gpsScore;
        String riskLevel;
        List<MetricDetail> details = new ArrayList<>();
    }

    @Data
    public static class MetricDetail{
        BiomechanicsMetric name;
        double value;
        double penalty;
        String recommendation;
    }

    public FinalReport computeReport(int age, String gender,
                                     double fhp, double qAngle, double shDiff,
                                     double stancePhase, double kneeFlexion, double cadence) {
        FinalReport report = new FinalReport();
        double totalWeightedScore = 0;
        double totalWeights = 0;

        double fhpMax = (age > BiomechanicalConstants.AGE_ELDERLY_THRESHOLD)
                ? BiomechanicalConstants.FHP_MAX_ELDERLY
                : BiomechanicalConstants.FHP_MAX_STANDARD;

        double sFHP = calculatePenalty(fhp, 0, fhpMax);
        totalWeightedScore += sFHP * BiomechanicalConstants.WEIGHT_CERVICAL_FHP;
        totalWeights += BiomechanicalConstants.WEIGHT_CERVICAL_FHP;

        addDetail(report, BiomechanicsMetric.FORWARD_HEAD_POSTURE, fhp, sFHP,
                sFHP > 0 ? BiomechanicalConstants.RECOMMENDATION_FHP : null);

        double qMin = gender.equalsIgnoreCase("MALE")
                ? BiomechanicalConstants.Q_ANGLE_MALE_MIN
                : BiomechanicalConstants.Q_ANGLE_FEMALE_MIN;
        double qMax = gender.equalsIgnoreCase("MALE")
                ? BiomechanicalConstants.Q_ANGLE_MALE_MAX
                : BiomechanicalConstants.Q_ANGLE_FEMALE_MAX;

        if (age > BiomechanicalConstants.AGE_ELDERLY_THRESHOLD) {
            qMin *= BiomechanicalConstants.AGE_ROM_ADJUSTMENT_FACTOR;
            qMax *= BiomechanicalConstants.AGE_TOLERANCE_INCREASE;
        }

        double sQ = calculatePenalty(qAngle, qMin, qMax);
        totalWeightedScore += sQ * BiomechanicalConstants.WEIGHT_Q_ANGLE;
        totalWeights += BiomechanicalConstants.WEIGHT_Q_ANGLE;

        addDetail(report, BiomechanicsMetric.Q_ANGLE, qAngle, sQ,
                sQ > 0 ? BiomechanicalConstants.RECOMMENDATION_Q_ANGLE : null);

        double sSh = calculatePenalty(shDiff, 0, BiomechanicalConstants.SHOULDER_SYMMETRY_MAX);
        totalWeightedScore += sSh * BiomechanicalConstants.WEIGHT_SHOULDER;
        totalWeights += BiomechanicalConstants.WEIGHT_SHOULDER;

        addDetail(report, BiomechanicsMetric.SHOULDER_SYMMETRY, shDiff, sSh,
                sSh > 0 ? BiomechanicalConstants.RECOMMENDATION_SHOULDER : null);

        double sStance = calculatePenalty(stancePhase,
                BiomechanicalConstants.STANCE_PHASE_MIN,
                BiomechanicalConstants.STANCE_PHASE_MAX);

        addDetail(report, BiomechanicsMetric.GAIT_STANCE_PHASE, stancePhase, sStance,
                sStance > 0 ? BiomechanicalConstants.RECOMMENDATION_GAIT_ASYMMETRY : null);

        double kneeMin = (age > BiomechanicalConstants.AGE_ELDERLY_THRESHOLD)
                ? BiomechanicalConstants.KNEE_FLEXION_MIN_ELDERLY
                : BiomechanicalConstants.KNEE_FLEXION_MIN_STANDARD;

        double sKnee = calculatePenalty(kneeFlexion, kneeMin,
                BiomechanicalConstants.KNEE_FLEXION_MAX);

        addDetail(report, BiomechanicsMetric.KNEE_FLEXION_SWING, kneeFlexion, sKnee,
                sKnee > 0 && kneeFlexion < kneeMin
                        ? BiomechanicalConstants.RECOMMENDATION_KNEE_FLEXION : null);


        double cadMin = (age > BiomechanicalConstants.AGE_ELDERLY_THRESHOLD)
                ? BiomechanicalConstants.CADENCE_MIN_ELDERLY
                : BiomechanicalConstants.CADENCE_MIN_STANDARD;

        double sCadence = calculatePenalty(cadence, cadMin, BiomechanicalConstants.CADENCE_MAX);

        addDetail(report, BiomechanicsMetric.CADENCE, cadence, sCadence,
                sCadence > 0 && cadence < cadMin
                        ? BiomechanicalConstants.RECOMMENDATION_CADENCE_LOW : null);

        report.gpsScore = (totalWeights > 0) ? (totalWeightedScore / totalWeights) : 0;

        if (report.gpsScore <= BiomechanicalConstants.RISK_OPTIMAL_MAX)
            report.riskLevel = "OPTIMAL";
        else if (report.gpsScore <= BiomechanicalConstants.RISK_MODERATE_MAX)
            report.riskLevel = "MODERATE";
        else
            report.riskLevel = "ELEVATED";

        return report;
    }

    private void addDetail(FinalReport report, BiomechanicsMetric name,
                           double val, double penalty, String rec) {
        MetricDetail md = new MetricDetail();
        md.setName(name);
        md.setValue(val);
        md.setPenalty(penalty);
        md.setRecommendation(rec);
        report.details.add(md);
    }

    private double calculatePenalty(double val, double min, double max){
        if (val >= min && val <= max) return 0.0;

        double limit = (val < min) ? min : max;
        double deviation = Math.abs(val - limit);

        return Math.min((deviation / limit) * BiomechanicalConstants.PENALTY_MULTIPLIER,
                BiomechanicalConstants.PENALTY_MAX_VALUE);
    }
}