package com.licenta.biomechanics_backend.service;

import com.licenta.biomechanics_backend.utils.Vector3DUtils;
import com.licenta.biomechanics_backend.utils.Vector3DUtils.Point3D;
import org.springframework.stereotype.Service;

@Service
public class BiomechanicsService {

    public double calculateQAngle(Point3D hip, Point3D knee, Point3D ankle){
        Point3D vFemur = Vector3DUtils.createVector(knee, hip);
        Point3D vTibia = Vector3DUtils.createVector(knee, ankle);
        double angle = Vector3DUtils.calculateAngle(vFemur, vTibia);
        return Math.abs(180 - angle);
    }

    public double calculateFHP(Point3D ear, Point3D shoulder) {
        Point3D vNeck = Vector3DUtils.createVector(shoulder, ear);
        Point3D vVertical = new Point3D(0, 0, 1);
        return Vector3DUtils.calculateAngle(vNeck, vVertical);
    }

    public double calculateShoulderDiff(Point3D left, Point3D right) {
        return Math.abs(left.z - right.z);
    }

    public double adjustThresholdForAge(double standardMax, int age) {
        return (age > 60) ? standardMax * 0.85 : standardMax;
    }
}
