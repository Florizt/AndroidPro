package com.florizt.androidpro

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import coil.load
import coil.transform.CircleCropTransformation
import com.florizt.androidpro.databinding.ActivityMainBinding
import com.florizt.base.delegate.viewBinding
import com.florizt.base.delegate.viewModelsLifecycle
import com.florizt.base.ui.ImmersionBars

@ImmersionBars
class MainActivity : AppCompatActivity() {

    private val binding by viewBinding<ActivityMainBinding>()
    private val viewModel by viewModelsLifecycle<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.iv.load(R.mipmap.ic_launcher){
            placeholder(R.drawable.ic_launcher_background)
            transformations(CircleCropTransformation())
        }
    }
}