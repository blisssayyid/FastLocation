package com.sayyid.library

import android.location.GpsStatus
import android.location.LocationManager
import com.tincher.arcgisuils.util.LocationEngineTool

/**
 * Created by sayyid on 2018/4/5.
 */
class GPSStatusListenerImpl(private val locationManager: LocationManager, private val listener: ILocationListener) : GpsStatus.Listener {
    override fun onGpsStatusChanged(event: Int) {
        /**
         * 卫星状态改变
         */
        if (GpsStatus.GPS_EVENT_SATELLITE_STATUS == event) {
            listener.onGpsEventStatusChanged(LocationEngineTool.getSatellites(locationManager, false))
        }
    }
}