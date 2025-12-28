package com.licenta.biomechanics_backend.logic;

import com.licenta.biomechanics_backend.model.enums.BiomechanicsMetric;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Component
public class ScoringEngine {

    private static final double W_NECK = 3.0;
    private static final double W_Q = 2.0;
    private static final double W_SHOULDER = 1.0;


    @Data
    public static class FinalReport {
        double gpsScore;
        String riskLevel;
        List<MetricDetail> details = new ArrayList<>();
    }

    @Data
    public static class MetricDetail {
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


        double fhpMax = (age > 60) ? 6.0 : 5.0;
        double sFHP = calculatePenalty(fhp, 0, fhpMax);

        totalWeightedScore += sFHP * W_NECK;
        totalWeights += W_NECK;

        addDetail(report, BiomechanicsMetric.FORWARD_HEAD_POSTURE, fhp, sFHP,
                sFHP > 0 ? "Exercise: Chin Tucks (Cervical Retraction)" : null);


        double qMin = gender.equalsIgnoreCase("MALE") ? 10 : 15;
        double qMax = gender.equalsIgnoreCase("MALE") ? 14 : 17;

        if (age > 60) { qMin *= 0.85; qMax *= 1.15; }

        double sQ = calculatePenalty(qAngle, qMin, qMax);
        totalWeightedScore += sQ * W_Q;
        totalWeights += W_Q;

        addDetail(report, BiomechanicsMetric.Q_ANGLE, qAngle, sQ,
                sQ > 0 ? "Exercise: Quadriceps Isometric Contraction" : null);


        double shMax = 0.015;
        double sSh = calculatePenalty(shDiff, 0, shMax);
        totalWeightedScore += sSh * W_SHOULDER;
        totalWeights += W_SHOULDER;

        addDetail(report, BiomechanicsMetric.SHOULDER_SYMMETRY, shDiff, sSh,
                sSh > 0 ? "Recommendation: Check backpack/bag carrying habits. Bilateral stretching." : null);

        double stanceMin = 57.0;
        double stanceMax = 63.0;
        double sStance = calculatePenalty(stancePhase, stanceMin, stanceMax);

        addDetail(report, BiomechanicsMetric.GAIT_STANCE_PHASE, stancePhase, sStance,
                sStance > 0 ? "Alert: Gait asymmetry. Possible antalgic gait (pain-avoidance pattern)." : null);


        double kneeMin = (age > 60) ? 55.0 : 60.0;
        double kneeMax = 70.0;
        double sKnee = calculatePenalty(kneeFlexion, kneeMin, kneeMax);

        addDetail(report, BiomechanicsMetric.KNEE_FLEXION_SWING, kneeFlexion, sKnee,
                sKnee > 0 && kneeFlexion < kneeMin ? "Risk: Insufficient flexion. Risk of tripping." : null);


        double cadMin = 100.0;
        double cadMax = 120.0;

        if (age > 60) cadMin = 90.0;

        double sCadence = calculatePenalty(cadence, cadMin, cadMax);

        addDetail(report, BiomechanicsMetric.CADENCE, cadence, sCadence,
                sCadence > 0 && cadence < cadMin ? "Alert: Low cadence. Balance exercises recommended." : null);
        report.gpsScore = (totalWeights > 0) ? (totalWeightedScore / totalWeights) : 0;


        if (report.gpsScore <= 20)
            report.riskLevel = "OPTIMAL";
        else if (report.gpsScore <= 50)
            report.riskLevel = "MODERATE";
        else
            report.riskLevel = "ELEVATED";

        return report;
    }

    private void addDetail(FinalReport report, BiomechanicsMetric name, double val, double penalty, String rec) {
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

        return Math.min((deviation / limit) * 10.0, 100.0);
    }
}