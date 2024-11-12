package io.github.nurani.network.connectivity.receiver

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import androidx.annotation.RequiresPermission
import io.github.nurani.network.connectivity.ConnectivityObserver
import io.github.nurani.network.connectivity.NetworkState.Companion.AVAILABLE
import io.github.nurani.network.connectivity.NetworkState.Companion.UNAVAILABLE
import io.github.nurani.network.connectivity.NetworkType.Companion.NONE

/**
 * Abstract BroadcastReceiver for monitoring network connectivity changes
 * on Android devices.
 *
 * This class is designed for SDK versions 16 to 21. It uses a
 * `BroadcastReceiver` to listen for connectivity changes and informs the
 * `ConnectivityObserver` of network state changes. Higher Android versions
 * can leverage `ConnectivityManager.NetworkCallback` for a more efficient
 * approach.
 *
 * Usage:
 * - Extend this class and provide an implementation for
 *   `connectivityObserver`.
 * - Ensure the necessary permissions are declared in the manifest:
 *    - `ACCESS_NETWORK_STATE`
 *    - `ACCESS_WIFI_STATE`
 *    - `INTERNET`
 */
internal abstract class ConnectivityReceiver : BroadcastReceiver() {

    /** The observer responsible for handling network connectivity changes. */
    abstract val connectivityObserver: ConnectivityObserver

    /**
     * Triggered when there is a change in network connectivity.
     *
     * This method listens for the `CONNECTIVITY_ACTION` broadcast, which is
     * sent when the network state changes. Based on the network type retrieved
     * from the observer, it notifies whether the network is available or
     * unavailable.
     *
     * @param context The Context in which the receiver is running.
     * @param intent The Intent being received.
     */
    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    final override fun onReceive(context: Context?, intent: Intent?) {
        if (ConnectivityManager.CONNECTIVITY_ACTION == intent?.action) {
            // Check the current network type from the observer
            val networkType = connectivityObserver.getNetworkType()

            // If no network is detected, notify observer with UNAVAILABLE state; otherwise, notify as AVAILABLE
            if (networkType == NONE) {
                connectivityObserver.onChange(UNAVAILABLE, NONE)
            } else {
                connectivityObserver.onChange(AVAILABLE, networkType)
            }
        }
    }
}
