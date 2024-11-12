package io.github.nurani.network.connectivity

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {
    val network: LiveData<Pair<Int, Int>> = MutableLiveData()

    fun setNetwork(state: Int, type: Int) {
        (network as MutableLiveData).value = Pair(state, type)
    }

}