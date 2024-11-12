package io.github.nurani.network.connectivity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import androidx.core.content.getSystemService
import io.github.nurani.network.connectivity.NetworkState.Companion.AVAILABLE
import io.github.nurani.network.connectivity.NetworkState.Companion.LOSING
import io.github.nurani.network.connectivity.NetworkState.Companion.LOST
import io.github.nurani.network.connectivity.NetworkState.Companion.UNAVAILABLE
import io.github.nurani.network.connectivity.NetworkType.Companion.BLUETOOTH
import io.github.nurani.network.connectivity.NetworkType.Companion.ETHERNET
import io.github.nurani.network.connectivity.NetworkType.Companion.LOWPAN
import io.github.nurani.network.connectivity.NetworkType.Companion.MOBILE
import io.github.nurani.network.connectivity.NetworkType.Companion.NONE
import io.github.nurani.network.connectivity.NetworkType.Companion.OTHER
import io.github.nurani.network.connectivity.NetworkType.Companion.SATELLITE
import io.github.nurani.network.connectivity.NetworkType.Companion.THREAD
import io.github.nurani.network.connectivity.NetworkType.Companion.USB
import io.github.nurani.network.connectivity.NetworkType.Companion.VPN
import io.github.nurani.network.connectivity.NetworkType.Companion.WIFI
import io.github.nurani.network.connectivity.NetworkType.Companion.WIFI_AWARE
import io.github.nurani.network.connectivity.receiver.ConnectivityReceiver


/**
 * An abstract class to observe network connectivity changes in an Android
 * application. Subclasses must implement the `onChange` method to handle
 * network state and type changes.
 *
 * @param context The context used to access system services.
 */
abstract class ConnectivityObserver(private val context: Context) {

    /** Flag indicating whether observing is currently registered. */
    private var isRegisteredObserving = false

    /** The ConnectivityManager used to access network information. */
    private val connectivityManager: ConnectivityManager =
        context.getSystemService<ConnectivityManager>()!!

    /**
     * Abstract method to handle network state and type changes. Must be
     * implemented by subclasses to respond to network changes.
     *
     * @param state The current network state (e.g., AVAILABLE, UNAVAILABLE).
     * @param networkType The type of the current network (e.g., WIFI, MOBILE).
     */
    protected abstract fun onChange(@NetworkState state: Int, @NetworkType networkType: Int)

    /**
     * Starts observing network connectivity changes. Registers the appropriate
     * network callback based on the Android SDK version.
     *
     * Requires the following permissions:
     * - Manifest.permission.ACCESS_NETWORK_STATE
     */
    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    open fun start() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // Use the default network callback for API 24 (Nougat) and above
            connectivityManager.registerDefaultNetworkCallback(connectivityCallback)
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // Register callback for specific network types for API levels below 24
            connectivityManager.registerNetworkCallback(
                NetworkRequest.Builder()
                    .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                    .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
                    .addTransportType(NetworkCapabilities.TRANSPORT_ETHERNET)
                    .addTransportType(NetworkCapabilities.TRANSPORT_BLUETOOTH)
                    .build(), connectivityCallback
            )
        } else {
            val intentFilter =
                @Suppress("DEPRECATION") IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
            context.registerReceiver(connectivityReceiver, intentFilter)
        }
        isRegisteredObserving = true

        // Emit the initial network state immediately
        val initialNetworkType = getNetworkType()
        onChange(if (initialNetworkType == NONE) UNAVAILABLE else AVAILABLE, initialNetworkType)
    }

    /**
     * Stops observing network connectivity changes by unregistering the
     * network callback.
     */
    open fun stop() {
        if (isRegisteredObserving) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                connectivityManager.unregisterNetworkCallback(connectivityCallback)
            } else {
                context.unregisterReceiver(connectivityReceiver)
            }
        }
    }

    /**
     * Retrieves the current network type.
     *
     * @return The type of the current network (e.g., WIFI, MOBILE) or NONE if
     *    no network is available.
     *
     * Requires the following permissions:
     * - Manifest.permission.ACCESS_NETWORK_STATE
     * - Manifest.permission.ACCESS_WIFI_STATE
     * - Manifest.permission.INTERNET
     */
    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    @Suppress("DEPRECATION")
    internal fun getNetworkType(): Int {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // For API 23 (Marshmallow) and above, use network capabilities
            val network = connectivityManager.activeNetwork ?: return NONE
            val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return NONE
            return when {
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> WIFI
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> MOBILE
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> ETHERNET
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> BLUETOOTH
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_VPN) -> VPN
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_THREAD) -> THREAD
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_LOWPAN) -> LOWPAN
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_USB) -> USB
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI_AWARE) -> WIFI_AWARE
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_SATELLITE) -> SATELLITE
                else -> OTHER
            }
        } else {
            // For API levels below Marshmallow, use deprecated APIs
            val networkInfo = connectivityManager.activeNetworkInfo ?: return NONE
            return when (networkInfo.type) {
                ConnectivityManager.TYPE_WIFI -> WIFI
                ConnectivityManager.TYPE_MOBILE -> MOBILE
                ConnectivityManager.TYPE_ETHERNET -> ETHERNET
                ConnectivityManager.TYPE_BLUETOOTH -> BLUETOOTH
                ConnectivityManager.TYPE_VPN -> VPN
                else -> OTHER
            }
        }
    }

    /** The network callback used to listen for network connectivity changes. */
    @SuppressLint("MissingPermission")
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private val connectivityCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onLosing(network: Network, maxMsToLive: Int) {
            // Triggered when the network is about to be lost
            onChange(LOSING, getNetworkType())
        }

        override fun onUnavailable() {
            // Triggered when the network becomes unavailable
            onChange(UNAVAILABLE, getNetworkType())
        }

        override fun onAvailable(network: Network) {
            // Triggered when the network becomes available
            onChange(AVAILABLE, getNetworkType())
        }

        override fun onLost(network: Network) {
            // Triggered when the network is lost
            onChange(LOST, getNetworkType())
        }
    }

    // Define an instance of ConnectivityReceiver with an inline implementation of ConnectivityObserver.
    // This allows `connectivityReceiver` to directly reference the current instance of ConnectivityObserver,
    // making it easier to handle network state changes within this observer context.
    private val connectivityReceiver = object : ConnectivityReceiver() {

        // Override the `connectivityObserver` property to return the current instance of ConnectivityObserver.
        // This ensures that network connectivity changes are handled by this specific observer instance.
        override val connectivityObserver: ConnectivityObserver
            get() = this@ConnectivityObserver

        override fun onChange(state: Int, networkType: Int) {
            this@ConnectivityObserver.onChange(state, networkType)
        }
    }

}

