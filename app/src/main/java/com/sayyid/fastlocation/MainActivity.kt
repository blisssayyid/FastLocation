package com.sayyid.fastlocation

import android.Manifest
import android.location.Location
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.sayyid.library.ILocationListener
import com.sayyid.library.LocationEngine
import kotlinx.android.synthetic.main.activity_main.*
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import java.text.SimpleDateFormat

class MainActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks, ILocationListener {
    private val RC_LOCATION: Int = 0
    private var locationEngine: LocationEngine? = null
    private var mLocation: Location? = null

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).setTitle("提示").setRationale("系统需要定位权限才能使用此功能").build().show()
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        when (requestCode) {
            RC_LOCATION -> {
                Toast.makeText(this, "权限获取成功", Toast.LENGTH_SHORT).show()
                locationEngine = LocationEngine().register(this, this)
            }
        }
    }

    private fun getNewTextView(content: String): TextView {
        return TextView(this).apply {
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            text = content
            setTextColor(this.resources.getColor(R.color.console_text_green))
            textSize = 12.0f
            setPadding(0, 3, 0, 0)
        }
    }

    override fun onLocationChanged(location: Location) {
        mLocation = location
        ll_root.addView(getNewTextView(
                "${location.latitude.toString()}, ${location.longitude.toString()}  " +
                        "${SimpleDateFormat("HH:mm:ss").format(location.time)} " +
                        "${location.provider} " +
                        "${location.accuracy}"))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (EasyPermissions.hasPermissions(this
                        , Manifest.permission.ACCESS_COARSE_LOCATION
                        , Manifest.permission.ACCESS_FINE_LOCATION)) {
            locationEngine = LocationEngine().register(this, this)
        } else {
            /**
             * 没有权限，进行权限申请
             */
            EasyPermissions.requestPermissions(this
                    , "应用获取位置需要定位权限"
                    , RC_LOCATION
                    , Manifest.permission.ACCESS_COARSE_LOCATION
                    , Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        locationEngine?.unRegister()
        locationEngine = null
    }
}
