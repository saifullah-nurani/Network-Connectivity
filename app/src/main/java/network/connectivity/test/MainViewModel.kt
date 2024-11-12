package network.connectivity.test

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.github.nurani.network.connectivity.NetworkState
import io.github.nurani.network.connectivity.NetworkType

class MainViewModel : ViewModel() {
    val network: MutableLiveData<Pair<String, String>> = MutableLiveData()
    fun onChangeNetwork(state: Int, type: Int) {
        network.postValue(
            Pair(
                when (state) {
                    NetworkState.AVAILABLE -> "Available"
                    NetworkState.UNAVAILABLE -> "Unavailable"
                    NetworkState.LOST -> "Lost"
                    NetworkState.LOSING -> "Losing"
                    else -> "Unavailable"
                }, when (type) {
                    NetworkType.WIFI -> "Wifi"
                    NetworkType.MOBILE -> "Mobile"
                    NetworkType.NONE -> "None"
                    NetworkType.ETHERNET -> "Ethernet"
                    NetworkType.BLUETOOTH -> "Bluetooth"
                    NetworkType.WIFI_AWARE->"jj"
                    else -> "Other"
                }
            )
        )
    }
}