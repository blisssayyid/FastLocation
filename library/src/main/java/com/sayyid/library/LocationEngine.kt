package com.sayyid.library

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.content.Context
import android.location.LocationManager
import androidx.annotation.RequiresPermission

/**
 * 定位引擎
 */
class LocationEngine {
    private val TAG = "LocationEngine"
    private var mLocationManagerList: MutableList<LocationManager> = mutableListOf()
    private var mLocationListener: LocationListenerImpl? = null
    private var mGPSStatusListener: GPSStatusListenerImpl? = null

    /**
     * 是否需要速度，默认不需要，因为不准确
     */
    var mIsSpeedRequired = false
    /**
     * 是否产生费用，也就是说是否通过运营商产生网络，默认产生
     */
    var mAllowNetWork = true
    /**
     * 是否需要方位信息，默认不需要
     */
    var mIsBearingRequired = false
    /**
     * 是否需要高度信息，默认不需要
     */
    var mIsAltitudeRequired = false
    /**
     * 是否只需要GPS, 默认不是
     */
    var onlyGPS = false
    /**
     * 是否替换 GPS 时间为系统时间，默认是
     * 因为有些设备的 GPS 时间获取异常
     */
    var replaceLocalTime = true
    /**
     * 精度范围内,默认没有
     */
    var mAccuracy = -1.0f
    /**
     * 设置触发 onLocationChanged 的条件
     *
     * 通知的最小时间间隔（以毫秒为单位）。此字段仅用作节省电量的提示，位置更新之间的实际时间可能大于或小于此值
     * 通知的最小时间间隔（以毫秒为单位）。此字段仅用作节省电量的提示，位置更新之间的实际时间可能大于或小于此值
     * 通知的最小时间间隔（以毫秒为单位）。此字段仅用作节省电量的提示，位置更新之间的实际时间可能大于或小于此值
     * 重要的事情说三遍
     *
     * 最小时间和最小距离,单位毫秒和米
     * 想要更长的距离和时间
     * @see com.tincher.arcgisuils.util.LocationEngine#mIntervalTime
     * @see com.tincher.arcgisuils.util.LocationEngine#mIntervalDistance
     */
    var minTime = 1000L
    var minDistance = 1.0f
    /**
     * 实际满足调用条件的最小时间和最小距离(闭区间）,同时满足触发
     * 单位毫秒和米
     * 默认都为0
     */
    var mIntervalTime = 0L
    var mIntervalDistance = 0.0


    @RequiresPermission(anyOf = [ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION])
    @SuppressLint("MissingPermission")
    fun register(context: Context, listener: ILocationListener): LocationEngine {
        val providers = context.getSystemService(Context.LOCATION_SERVICE).let { it as LocationManager }.getProviders(true)
        this.mLocationListener = LocationListenerImpl(this, listener)
        for (provider: String in providers) {
            if (!mAllowNetWork && LocationManager.NETWORK_PROVIDER == provider) {
                /**
                 * 假如不允许网络，那么就不创建网络的 provider
                 * 然而这好像不并管用
                 */
                continue
            }
            var locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            if (LocationManager.GPS_PROVIDER == provider) {
                mGPSStatusListener = GPSStatusListenerImpl(locationManager, listener)
                locationManager.addGpsStatusListener(mGPSStatusListener)
            }
            locationManager.requestLocationUpdates(provider, this.minTime, this.minDistance, this.mLocationListener)
            mLocationManagerList.add(locationManager)
        }
        return this
    }

    @RequiresPermission(anyOf = [ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION])
    @SuppressLint("MissingPermission")
    fun unRegister(): LocationEngine {
        for (i in mLocationManagerList.indices) {
            mLocationManagerList.get(i).removeUpdates(this.mLocationListener)
        }
        this.mLocationListener = null
        mLocationManagerList.clear()
        return this
    }

    /**
     * 获得 LocationManagers
     */
    fun getLocationManager(): List<LocationManager> {
        return mLocationManagerList
    }


    /**
     * 设置是否需要速度
     */
    fun isSpeedRequired(isSpeedRequired: Boolean): LocationEngine {
        this.mIsSpeedRequired = isSpeedRequired
        return this
    }

    /**
     * 设置是否产生费用（网络）
     */
    fun allowNetWork(allowNetWork: Boolean): LocationEngine {
        this.mAllowNetWork = allowNetWork
        return this
    }

    /**
     * 设置是否提供方位信息
     */
    fun isBearingRequired(isBearingRequired: Boolean): LocationEngine {
        this.mIsBearingRequired = isBearingRequired
        return this
    }

    /**
     * 设置精度，范围 米
     */
    fun accuracy(accuracy: Float): LocationEngine {
        this.mAccuracy = accuracy
        return this
    }

    /**
     * 设置是否提供高度信息
     */
    fun isAltitudeRequired(isAltitudeRequired: Boolean): LocationEngine {
        this.mIsAltitudeRequired = isAltitudeRequired
        return this
    }

    /**
     * 设置触发变动的最小间隔秒数，单位毫秒
     * 一般不进行变更，如果想省电可以做变动
     */
    fun minTime(minTime: Long): LocationEngine {
        this.minTime = minTime
        return this
    }

    /**
     * 设置触发变动的最小间隔长度，单位米
     * 一般不进行变更，如果想省电可以做变动
     */
    fun minDistance(minDistance: Float): LocationEngine {
        this.minDistance = minDistance
        return this
    }

    /**
     * 设置间隔秒数
     */
    fun intervalTime(intervalTime: Long): LocationEngine {
        this.mIntervalTime = intervalTime
        return this
    }

    /**
     * 设置间隔长度,单位米
     */
    fun intervalDistance(intervalDistance: Double): LocationEngine {
        this.mIntervalDistance = intervalDistance
        return this
    }

    /**
     * 是否只需要 GPS 结果
     */
    fun onlyGPS(onlyGPS: Boolean): LocationEngine {
        this.onlyGPS = onlyGPS
        return this
    }

    /**
     * 是否替换 GPS 时间为系统时间，有些设备的 GPS 时间获取异常
     */
    fun replaceLocalTime(replaceLocalTime: Boolean): LocationEngine {
        this.replaceLocalTime = replaceLocalTime
        return this
    }
}