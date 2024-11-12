package io.github.nurani.network.connectivity

import android.Manifest
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.annotation.RequiresPermission
import androidx.core.content.getSystemService
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleObserver

@RequiresPermission(
    allOf = [Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.INTERNET]
)
inline fun <reified T : Context> T.connectivityObserver(
    // A callback that will be invoked whenever there is a change in the network state or type
    crossinline onChange: (state: @NetworkState Int, networkType: @NetworkType Int) -> Unit
): ConnectivityObserver {
    // Return a ConnectivityObserver instance that listens for changes and calls the provided onChange callback
    return object : ConnectivityObserver(this) {
        // Override the onChange method to call the callback with updated network state and type
        override fun onChange(state: Int, networkType: Int) {
            onChange(state, networkType) // Trigger the provided callback
        }
    }
}

@RequiresPermission(
    allOf = [Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.INTERNET]
)
inline fun <reified T : Context> T.connectivityObserver(
    // The lifecycle object (from Activity or Fragment) to bind the observer to
    lifecycle: Lifecycle,
    // The specific lifecycle state (e.g., STARTED, RESUMED) when network observation should start
    observingState: Lifecycle.State,
    // A callback triggered on network state changes
    crossinline onChange: (state: @NetworkState Int, networkType: @NetworkType Int) -> Unit
) {
    // Create a ConnectivityObserver instance that will call the onChange callback
    val connectivityObserver = connectivityObserver(onChange)

    // Declare a variable to hold the lifecycle observer
    var lifecycleObserver: LifecycleObserver? = null

    // Create a LifecycleEventObserver to observe lifecycle changes and start/stop network observation accordingly
    lifecycleObserver = LifecycleEventObserver { _, event ->
        when {
            // Start observing network changes when the lifecycle reaches the specified state (e.g., STARTED)
            observingState == event.targetState -> connectivityObserver.start()
            else -> {
                // Stop observing network changes when the lifecycle moves to a non-relevant state
                when (event) {
                    // On lifecycle destruction, remove the observer to avoid memory leaks
                    Lifecycle.Event.ON_DESTROY -> {
                        lifecycleObserver?.let { lifecycle.removeObserver(it) }
                    }

                    // On lifecycle stop or pause, stop the connectivity observer
                    Lifecycle.Event.ON_STOP, Lifecycle.Event.ON_PAUSE -> connectivityObserver.stop()
                    else -> {} // Other events do nothing
                }
            }
        }
    }

    // Add the LifecycleEventObserver to the lifecycle of the component (e.g., Activity or Fragment)
    lifecycle.addObserver(lifecycleObserver)
}

/**
 * Checks if the network is currently connected.
 *
 * @return True if any network is connected, false otherwise.
 * @receiver The context used to access system services.
 */
@Suppress("DEPRECATION")
@RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
inline fun<reified T:Context> T.isNetworkConnected(): Boolean {
    val connectivityManager = getSystemService<ConnectivityManager>() ?: return false

    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        // For API 23 (Marshmallow) and above, use network capabilities
        val network = connectivityManager.activeNetwork ?: return false
        val networkCapabilities =
            connectivityManager.getNetworkCapabilities(network) ?: return false
        networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    } else {
        // For API levels below Marshmallow, use deprecated methods
        val networkInfo = connectivityManager.activeNetworkInfo ?: return false
        networkInfo.isConnected
    }
}

/**
 * Checks if the current network is Wi-Fi.
 *
 * @return True if connected to Wi-Fi, false otherwise.
 * @receiver The context used to access system services.
 */
@Suppress("DEPRECATION")
@RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
inline fun<reified T:Context> T.isNetworkWifi(): Boolean {
    val connectivityManager = getSystemService<ConnectivityManager>()!!
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val network = connectivityManager.activeNetwork ?: return false
        val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
        return activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
    } else {
        val networkInfo = connectivityManager.activeNetworkInfo ?: return false
        return networkInfo.type == ConnectivityManager.TYPE_WIFI
    }
}

/**
 * Checks if the current network is Mobile (Cellular).
 *
 * @return True if connected to Mobile, false otherwise.
 * @receiver The context used to access system services.
 */
@Suppress("DEPRECATION")
@RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
inline fun<reified T:Context> T.isNetworkMobile(): Boolean {
    val connectivityManager = getSystemService<ConnectivityManager>()!!
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val network = connectivityManager.activeNetwork ?: return false
        val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
        return activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
    } else {
        val networkInfo = connectivityManager.activeNetworkInfo ?: return false
        return networkInfo.type == ConnectivityManager.TYPE_MOBILE
    }
}

/**
 * Checks if the current network is Ethernet.
 *
 * @return True if connected to Ethernet, false otherwise.
 * @receiver The context used to access system services.
 */
@Suppress("DEPRECATION")
@RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
inline fun<reified T:Context> T.isNetworkEthernet(): Boolean {
    val connectivityManager = getSystemService<ConnectivityManager>()!!
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val network = connectivityManager.activeNetwork ?: return false
        val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
        return activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
    } else {
        val networkInfo = connectivityManager.activeNetworkInfo ?: return false
        return networkInfo.type == ConnectivityManager.TYPE_ETHERNET
    }
}

/**
 * Checks if the current network is Bluetooth.
 *
 * @return True if connected to Bluetooth, false otherwise.
 * @receiver The context used to access system services.
 */
@RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
inline fun<reified T:Context> T.isNetworkBluetooth(): Boolean {
    val connectivityManager = getSystemService<ConnectivityManager>()!!
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val network = connectivityManager.activeNetwork ?: return false
        val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
        return activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH)
    } else {
        // Bluetooth is not supported on API levels below Marshmallow in the same way
        // Return false or handle as needed
        return false
    }
}



