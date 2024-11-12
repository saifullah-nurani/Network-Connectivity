package io.github.nurani.network.connectivity

import android.annotation.TargetApi
import android.os.Build
import androidx.annotation.IntDef
import io.github.nurani.network.connectivity.NetworkType.Companion.BLUETOOTH
import io.github.nurani.network.connectivity.NetworkType.Companion.ETHERNET
import io.github.nurani.network.connectivity.NetworkType.Companion.MOBILE
import io.github.nurani.network.connectivity.NetworkType.Companion.NONE
import io.github.nurani.network.connectivity.NetworkType.Companion.VPN
import io.github.nurani.network.connectivity.NetworkType.Companion.WIFI

@Target(AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.FIELD, AnnotationTarget.TYPE)
@Retention(AnnotationRetention.RUNTIME)
@IntDef(NONE, WIFI, MOBILE, VPN, BLUETOOTH, ETHERNET)
annotation class NetworkType {
    companion object {
        internal const val OTHER = 0x0000
        const val NONE = 0x0001
        const val WIFI = 0x0002
        const val MOBILE = 0x0003
        const val BLUETOOTH = 0x0004
        const val ETHERNET = 0x0005
        const val VPN = 0x0006

        @TargetApi(Build.VERSION_CODES.M)
        const val THREAD = 0x0007

        @TargetApi(Build.VERSION_CODES.M)
        const val LOWPAN = 0x0008

        @TargetApi(Build.VERSION_CODES.M)
        const val SATELLITE = 0x0009

        @TargetApi(Build.VERSION_CODES.M)
        const val USB = 0x0010

        @TargetApi(Build.VERSION_CODES.M)
        const val WIFI_AWARE = 0x00011
    }
}
