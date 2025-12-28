package com.licenta.biomechanics_backend.utils;

public class Vector3DUtils {

    public static class Point3D {
        public double x, y, z;
        public Point3D(double x, double y, double z) { this.x = x; this.y = y; this.z = z; }
    }

    public static Point3D createVector(Point3D start, Point3D end){
        return new Point3D(end.x - start.x, end.y - start.y, end.z - start.z);
    }

    public static double getMagnitude(Point3D v) {
        return Math.sqrt(v.x * v.x + v.y * v.y + v.z * v.z);
    }

    public static double dotProduct(Point3D v1, Point3D v2) {
        return v1.x * v2.x + v1.y * v2.y + v1.z * v2.z;
    }

    public static double calculateAngle(Point3D v1, Point3D v2){
        double dot = dotProduct(v1, v2);
        double mag1 = getMagnitude(v1);
        double mag2 = getMagnitude(v2);
        if (mag1 == 0 || mag2 == 0) return 0.0;

        double cosTheta = Math.max(-1.0, Math.min(1.0, dot / (mag1 * mag2)));
        return Math.toDegrees(Math.acos(cosTheta));
    }
}
