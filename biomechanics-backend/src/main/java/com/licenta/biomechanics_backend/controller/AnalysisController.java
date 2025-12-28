package com.licenta.biomechanics_backend.controller;

import com.licenta.biomechanics_backend.logic.ScoringEngine;
import com.licenta.biomechanics_backend.model.*;
import com.licenta.biomechanics_backend.repository.*;
import com.licenta.biomechanics_backend.service.BiomechanicsService;
import com.licenta.biomechanics_backend.utils.Vector3DUtils.Point3D;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.licenta.biomechanics_backend.mapper.Point3DMapper.mapToPoint;

@RestController
@RequestMapping("/api/analysis")
public class AnalysisController {

    @Autowired
    private BiomechanicsService bioService;

    @Autowired
    private ScoringEngine scoringEngine;

    @Autowired
    private AssessmentRepository assessmentRepo;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private MetricResultRepository metricRepo;

    @PostMapping("/process/{userId}")
    public ResponseEntity<?> processScanResult(@PathVariable Long userId,
                                               @RequestBody Map<String, Object> pythonData) {


        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));


        int age = Period.between(user.getDateOfBirth(), LocalDate.now()).getYears();
        String gender = user.getGender().name();



        Map<String, Object> rawKeypoints = null;
        Object kpObj = pythonData.get("keypoints");
        if (kpObj instanceof Map) {
            rawKeypoints = (Map<String, Object>) kpObj;
        } else if (pythonData instanceof Map) {

            rawKeypoints = (Map<String, Object>) pythonData;
        }

        if (rawKeypoints == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "keypoints not provided or invalid format"));
        }

        Point3D earL = mapToPoint((Map<String, ?>) rawKeypoints.get("l_ear"));
        Point3D shoulderL = mapToPoint((Map<String, ?>) rawKeypoints.get("l_shoulder"));
        Point3D shoulderR = mapToPoint((Map<String, ?>) rawKeypoints.get("r_shoulder"));


        Point3D hipL = mapToPoint((Map<String, ?>) rawKeypoints.get("l_hip"));
        Point3D kneeL = mapToPoint((Map<String, ?>) rawKeypoints.get("l_knee"));
        Point3D ankleL = mapToPoint((Map<String, ?>) rawKeypoints.get("l_ankle"));


        double fhp = bioService.calculateFHP(earL, shoulderL);


        double qAngle = bioService.calculateQAngle(hipL, kneeL, ankleL);


        double shDiff = bioService.calculateShoulderDiff(shoulderL, shoulderR);


        Map<String, Object> meta = null;
        Object metaObj = pythonData.get("meta");
        if (metaObj instanceof Map) meta = (Map<String, Object>) metaObj;

        double stancePhase = meta != null && meta.containsKey("stance_phase") ? toDouble(meta.get("stance_phase")) : 60.0;
        double kneeFlexion = meta != null && meta.containsKey("knee_flexion") ? toDouble(meta.get("knee_flexion")) : 65.0;
        double cadence = meta != null && meta.containsKey("cadence") ? toDouble(meta.get("cadence")) : 110.0;


        ScoringEngine.FinalReport report = scoringEngine.computeReport(
                age, gender, fhp, qAngle, shDiff, stancePhase, kneeFlexion, cadence
        );


        double evolutionPercentage = calculateEvolution(userId, report.getGpsScore());


        Assessment assessment = new Assessment();
        assessment.setUser(user);
        assessment.setCreatedAt(LocalDateTime.now());
        assessment.setGlobalPostureScore(report.getGpsScore());
        assessment.setRiskLevel(report.getRiskLevel());

        Assessment savedAssessment = assessmentRepo.save(assessment);

        List<MetricResult> resultsToSave = new ArrayList<>();
        for (ScoringEngine.MetricDetail detail : report.getDetails()) {
            MetricResult mr = new MetricResult();
            mr.setAssessment(savedAssessment);

            mr.setMetricType(detail.getName());

            mr.setMeasuredValue(detail.getValue());
            mr.setPenaltyScore(detail.getPenalty());
            mr.setStatusLabel(detail.getPenalty() == 0 ? "Normal" : "Deviated");
            mr.setRecommendation(detail.getRecommendation());

            resultsToSave.add(mr);
        }
        metricRepo.saveAll(resultsToSave);


        return ResponseEntity.ok(Map.of(
                "assessmentId", savedAssessment.getId(),
                "gpsScore", report.getGpsScore(),
                "riskLevel", report.getRiskLevel(),
                "evolution", evolutionPercentage,
                "metrics", report.getDetails()
        ));
    }



    private double calculateEvolution(Long userId, double currentScore) {

        List<Assessment> history = assessmentRepo.findByUserIdOrderByCreatedAtDesc(userId);

        if (history.isEmpty()) {
            return 0.0;
        }


        Assessment initialAssessment = history.get(history.size() - 1);
        double initialScore = initialAssessment.getGlobalPostureScore();

        if (initialScore == 0) return 0.0;


        return ((currentScore - initialScore) / initialScore) * 100.0;
    }

    private double toDouble(Object o) {
        if (o == null) return 0.0;
        if (o instanceof Number) return ((Number) o).doubleValue();
        try { return Double.parseDouble(o.toString()); } catch (Exception e) { return 0.0; }
    }
}

