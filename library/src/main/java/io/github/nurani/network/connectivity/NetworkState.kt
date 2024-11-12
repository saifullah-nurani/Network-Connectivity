package io.github.nurani.network.connectivity

import androidx.annotation.IntDef
import io.github.nurani.network.connectivity.NetworkState.Companion.AVAILABLE
import io.github.nurani.network.connectivity.NetworkState.Companion.LOSING
import io.github.nurani.network.connectivity.NetworkState.Companion.LOST
import io.github.nurani.network.connectivity.NetworkState.Companion.UNAVAILABLE

@Target(AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.FIELD, AnnotationTarget.TYPE)
@Retention(AnnotationRetention.RUNTIME)
@IntDef(AVAILABLE, UNAVAILABLE, LOST, LOSING)
annotation class NetworkState {
    companion object {
        const val AVAILABLE = 0x8888
        const val UNAVAILABLE = 0x8889
        const val LOST = 0x8890
        const val LOSING = 0x8891
    }
}
