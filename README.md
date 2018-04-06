# FastLocation

[![](https://jitpack.io/v/blisssayyid/FastLocation.svg)](https://jitpack.io/#blisssayyid/FastLocation)

Helps you quickly implement positioning with Native API, Simple, fast and convenient

Native API positioning is usually used to get the provider through `getBestProvider`
, and then through `requestLocationUpdates` to request positioning, but this will
lead to the following three problems:
1. `requestLocationUpdates` incoming trigger the shortest time and distance is unstable
2. through `getBestProvider` to get the position is only the most accurate position,
that is, if you agree to through network positioning, but GPS is open, the system will
only give you feedback GPS results
3. special mobile phone manufacturers own custom system, there may be abnormal location time

### Start

There are only four steps you can quickly implement the positioning function

##### Gradle:

Add it in your root build.gradle at the end of repositories:
```
allprojects {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}
```
And add the dependency
```
dependencies {
    implementation 'com.github.blisssayyid:FastLocation:0.0.1'
}
```
#### Base
##### Step1
Register permissions in the `AndroidManifest.xml`

``` XML
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
```

##### Step2
Registration engine, if you are API 26+, need to check runtime permissions

``` Kotlin
mLocationEngine = LocationEngine().register(this, this)
```

##### Step3
Implement ILocationListener Interface, it has five methods that you can implement separately
* `onLocationChanged(location: Location)`
This method is called by a location change when the condition is met
* `onStatusChanged(provider: String?, status: Int, extras: Bundle?)`
Called when the provider status changes. This method is called when a provider
is unable to fetch a location or if the provider has recently become
available after a period of unavailability.
* `onProviderEnabled(provider: String?)`
Called when the provider is enabled by the user.
* `onProviderDisabled(provider: String?)`
Called when the provider is disabled by the user. If requestLocationUpdates
is called on an already disabled provider
, this method is called immediately.
* `onGpsEventStatusChanged(allSatellites: List<GpsSatellite>)`
Called When the GPS satellite state changes

``` Kotlin
class MainActivity : AppCompatActivity(), ILocationListener{
    fun onLocationChanged(location: Location)
    fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
    fun onProviderEnabled(provider: String?){}
    fun onProviderDisabled(provider: String?){}
    fun onGpsEventStatusChanged(allSatellites: List<GpsSatellite>){}
}
```

##### Step4
Destroy in the life cycle, otherwise it will cause a memory leak

``` Kotlin
override fun onDestroy() {
    super.onDestroy()
    mLocationEngine?.unRegister()
    mLocationEngine = null
}
```
#### Advanced

You can adapt to a variety of configurations in the process

``` Kotlin
/**
 * All of the following refer to filtering, not shielding
 * All of the following refer to filtering, not shielding
 * All of the following refer to filtering, not shielding
 */
locationEngine = LocationEngine()
    // Whether to allow the network location results on the onLocationChanged() callback, the default true
    .allowNetWork(true)
    // Whether only to allow the GPS location results on the onLocationChanged() callback, the default false
    .onlyGPS(false)
    // Whether to allow altitude information, no altitude information is not allowed to enter the onLocationChanged() callback, the default false
    .isAltitudeRequired(true)
    // Whether to allow bearing information, no bearing information is not allowed to enter the onLocationChanged() callback, the default false
    .isBearingRequired(true)
    // Whether to allow speed information, no speed information is not allowed to enter the onLocationChanged() callback, the default false
    .isSpeedRequired(true)
    // Minimum distance, and intervalTime() meet at the same time to make onLocationChanged() callback, the default 0.0
    .intervalDistance(2.0)
    // Minimum time, and intervalDistance() meet at the same distance to make onLocationChanged() callback, the default 0L
    .intervalTime(1000 * 5)
    // Minimum onLocationChanged() call precision, closed interval, default - 1.0f
    .accuracy(20.0f)
    // Minimum distance when applying for positioning, unless you want to save power, generally do not change
    // You should focus on changing intervalDistance(), default 1.0f
    .minDistance(1.0f)
    // Minimum time when applying for positioning, unless you want to save power, generally do not change
    // You should focus on changing intervalTime(), default 1000L
    .minTime(1000)
    // Do you want to replace GPS coordinate time with native time
    // , because some systems are too custom to cause confusion, the default is true
    .replaceLocalTime(true)
    .register(this, this)
```

#### Other

You can use `LocationEngineTool`

```
// Gets the distance between two longitudes and latitudes
fun distance(latA: Double, logA: Double, latB: Double, logB: Double): Double
// Gets the distance between two longitudes and latitudes
fun distance(locationA: Location, locationB: Location): Double
// Determine if GPS is available
fun isGpsEnabled(locationManager: LocationManager): Boolean
// Determine if NetWork or GPS is available
fun isLocationEnabled(locationManager: LocationManager): Boolean
// Number of acquisition satellites, The second parameter indicates whether only the most recently corrected satellite is returned
fun getSatellites(locationManager: LocationManager, usedInFix: Boolean): List<GpsSatellite>
```