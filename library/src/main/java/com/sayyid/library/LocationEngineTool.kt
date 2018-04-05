package com.tincher.arcgisuils.util

import android.annotation.SuppressLint
import android.location.GpsSatellite
import android.location.Location
import android.location.LocationManager

/**
 * Created by sayyid on 2018/4/2.
 */
object LocationEngineTool {
    /**
     * 计算两个经纬度之间的距离
     *
     * @param latA latitudeA 第一个坐标的纬度
     * @param logA longtitudeA 第一个坐标的经度
     * @param latB latitudeB 第二个坐标的纬度
     * @param logB longtitudeB 第二个坐标的经度
     * @return 两个经纬度之间的距离
     */
    fun distance(latA: Double, logA: Double, latB: Double, logB: Double): Double {
        val earthR = 6378137.0
        val x = Math.cos(latA * Math.PI / 180) * Math.cos(latB * Math.PI / 180) * Math.cos((logA - logB) * Math.PI / 180)
        val y = Math.sin(latA * Math.PI / 180) * Math.sin(latB * Math.PI / 180)
        var s = x + y
        if (s > 1)
            s = 1.0
        if (s < -1)
            s = -1.0
        val alpha = Math.acos(s)
        return alpha * earthR
    }

    /**
     * 计算两个经纬度之间的距离
     *
     * @param locationA 第一个坐标
     * @param locationB 第二个坐标
     * @return 两个经纬度之间的距离
     */
    fun distance(locationA: Location, locationB: Location): Double {
        return distance(locationA.latitude, locationA.longitude, locationB.latitude, locationB.longitude)
    }

    /**
     * 判断Gps是否可用
     *
     * @return `true`: 是 `false`: 否
     */
    fun isGpsEnabled(locationManager: LocationManager): Boolean {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    /**
     * 判断定位是否可用
     *
     * @return `true`: 是 `false`: 否
     */
    fun isLocationEnabled(locationManager: LocationManager): Boolean {
        return locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) || locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    /**
     * 获得获取到的卫星数量
     *
     * @param context
     * @return 卫星数量
     */
    @SuppressLint("MissingPermission")
    fun getSatellites(locationManager: LocationManager, usedInFix: Boolean): List<GpsSatellite> {
        val result = ArrayList<GpsSatellite>()
        val resultAll = ArrayList<GpsSatellite>()
        //获取当前状态
        val gpsStatus = locationManager.getGpsStatus(null)
        //获取卫星颗数的默认最大值
        val maxSatellites = gpsStatus.maxSatellites
        //创建一个迭代器保存所有卫星
        val iters = gpsStatus.satellites.iterator()
        var count = 0
        while (iters.hasNext() && count <= maxSatellites) {
            count++
            val s = iters.next()
            resultAll.add(s)
            if (s.usedInFix()) {
                result.add(s)
            }
        }

        return if (usedInFix) {
            result
        } else {
            resultAll
        }
    }
}