package com.sayyid.library

import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import com.tincher.arcgisuils.util.LocationEngineTool

/**
 * Created by sayyid on 2018/3/28.
 */
class LocationListenerImpl(private val locationEngine: LocationEngine, private val listener: ILocationListener) : LocationListener {
    private var mPreLocation: Location? = null

    /**
     * 只返回不等于空的，这里进行各种过滤
     * 这里根据过滤条件时间上是不稳定的，不能做定时器来用
     */
    @Synchronized
    override fun onLocationChanged(location: Location?) {
        /**
         * 个别机型 GPS 时间不可靠，需要自己计算时间
         */
        val currentTime = System.currentTimeMillis()
        if (locationEngine.replaceLocalTime) {
            location?.time = currentTime
        }
        if (!locationEngine.mAllowNetWork && LocationManager.NETWORK_PROVIDER == location?.provider) {
            /**
             * 不允许走网络则不允许 networkprovider 提供的
             */
            return
        }
        if (locationEngine.onlyGPS && LocationManager.GPS_PROVIDER != location?.provider) {
            /**
             * 只需要 GPS 结果但是结果不是 GPS 的
             */
            return
        }
        if (locationEngine.mIsSpeedRequired && location?.hasSpeed() == false) {
            /**
             * 有速度需要但是没有速度的
             */
            return
        }
        if (locationEngine.mIsBearingRequired && location?.hasBearing() == false) {
            /**
             * 需要精度信息但是没有的
             */
            return
        }
        if (locationEngine.mIsAltitudeRequired && location?.hasAltitude() == false) {
            /**
             * 需要高度信息但是没有的
             */
            return
        }
        if (locationEngine.mAccuracy >= 0.0f
                && (location?.hasAccuracy() == false || location?.accuracy ?: 9999.9f > locationEngine.mAccuracy)) {
            /**
             * 如果有范围大于0,并且没有精度
             *              ,或者有精度并且精度大于范围的
             */
            return
        }
        if (location != null && mPreLocation != null) {
            val distance = Math.abs(LocationEngineTool.distance(mPreLocation!!, location))
            val interval = Math.abs(currentTime - mPreLocation!!.time)
            if (distance < locationEngine.mIntervalDistance || interval < locationEngine.mIntervalTime) {
                /**
                 * 间隔距离和时间其中一个条件不满足
                 */
                return
            }
        }
        location?.let {
            listener.onLocationChanged(it)
            mPreLocation = it
            /**
             * 个别机型 GPS 时间不可靠，需要自己计算时间
             */
            mPreLocation?.time = currentTime
        }
    }

    @Synchronized
    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
        listener.onStatusChanged(provider, status, extras)
    }

    @Synchronized
    override fun onProviderEnabled(provider: String?) {
        listener.onProviderEnabled(provider)
    }

    @Synchronized
    override fun onProviderDisabled(provider: String?) {
        listener.onProviderDisabled(provider)
    }
}