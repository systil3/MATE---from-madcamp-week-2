package com.example.week2
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
class Distance {
    fun haversine(lat1: Double, lon1: Double,
                  lat2: Double, lon2: Double): Double {
        val R = 6371.0 // 지구의 반지름 (단위: 킬로미터)

        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)

        val a = sin(dLat / 2) * sin(dLat / 2) + cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        // m 단위로 계산
        return R * c * 1000
    }

    fun distanceToString(dist: Double): String {
        //dist : m 단위

        if(dist > 1000000) {
            return "1000 km 이상"
        }pu
        else if(dist > 1000) {
            return "${String.format("%.1f", dist / 1000).toDouble()} km"
        }
        else {
            return "${String.format("%.1f", dist).toDouble()} m"
        }
    }
}