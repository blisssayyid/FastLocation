package com.sayyid.library

import android.location.GpsSatellite
import android.location.Location
import android.os.Bundle

/**
 * Created by sayyid on 2018/3/28.
 */
interface ILocationListener {
    /**
     * 获得最后的位置
     */
    fun getLastLocation(location: Location): Location = location
    /**
     * 这里根据过滤条件时间上是不稳定的，不能做定时器来用
     */
    fun onLocationChanged(location: Location): Unit {
        getLastLocation(location)
    }
    /**
     * 当定位状态改变的时候
     */
    fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
    /**
     * 当 Provider 开启的时候
     */
    fun onProviderEnabled(provider: String?) {}
    /**
     * 当 Provider 关闭的时候
     */
    fun onProviderDisabled(provider: String?) {}
    /**
     * 当 GPS 卫星事件变更的时候
     */
    fun onGpsEventStatusChanged(allSatellites: List<GpsSatellite>) {}
}