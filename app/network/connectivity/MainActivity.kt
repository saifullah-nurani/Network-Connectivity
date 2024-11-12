package io.github.nurani.network.connectivity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import io.github.nurani.network.state.R
import io.github.nurani.network.state.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val binding = DataBindingUtil.setContentView<ActivityMainBinding>(this)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
//        binding.lifecycleOwner = this
//        binding.viewModel = viewModel

        // observing network based on lifecycle
//        connectivityObserver(lifecycle, Lifecycle.State.RESUMED, viewModel::setNetwork)

    }
}