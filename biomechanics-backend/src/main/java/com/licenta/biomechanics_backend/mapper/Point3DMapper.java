package com.licenta.biomechanics_backend.mapper;

import com.licenta.biomechanics_backend.utils.Vector3DUtils;
import com.licenta.biomechanics_backend.utils.Vector3DUtils.Point3D;

import java.util.Map;

public  class Point3DMapper {


    public static Vector3DUtils.Point3D mapToPoint(Map<String, ?> map) {
        if (map == null) return new Point3D(0,0,0);
        return new Vector3DUtils.Point3D(
                toDouble(map.get("x")),
                toDouble(map.get("y")),
                toDouble(map.get("z"))
        );
    }

    private static double toDouble(Object o) {
        if (o == null) return 0.0;
        if (o instanceof Number) {
            return ((Number) o).doubleValue();
        }
        try {
            return Double.parseDouble(o.toString());
        } catch (Exception e) {
            return 0.0;
        }
    }
}
