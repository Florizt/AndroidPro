package com.florizt.androidpro

import android.Manifest
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.florizt.androidpro.databinding.ActivityMainBinding
import com.florizt.base.permission.AndroidProPermission
import com.florizt.base.delegate.viewBinding
import com.florizt.base.delegate.viewModelsLifecycle
import com.florizt.base.ui.ImmersionBars

@ImmersionBars
class MainActivity : AppCompatActivity() {

    private val binding by viewBinding<ActivityMainBinding>()
    private val viewModel by viewModelsLifecycle<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding
        AndroidProPermission.requestPermission(
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
            granted = {
                println("------------- 111")
            },
            rationale = {
                println("------------- 222  $it")
            },
            denied = {
                println("------------- 333  $it")
            }
        )
    }

    fun onKeyboardChange (
        isPopup: Boolean,
        keyboardHeight: Int
    ){
        println("============ $isPopup $keyboardHeight")
    }
}