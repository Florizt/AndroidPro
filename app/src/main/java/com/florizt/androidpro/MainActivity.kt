package com.florizt.androidpro

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.marginTop
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2.ORIENTATION_VERTICAL
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.florizt.androidpro.databinding.ActivityMainBinding
import com.florizt.androidpro.databinding.TestBinding
import com.florizt.androidpro.databinding.TestDialogBinding
import com.florizt.base.delegate.viewBinding
import com.florizt.base.delegate.viewModelsLifecycle
import com.florizt.base.ext.dp
import com.florizt.base.ext.dpf
import com.florizt.base.ext.startActivity
import com.florizt.base.ext.string
import com.florizt.base.repository.net.entity.ApiResponse
import com.florizt.base.repository.net.entity.Resource
import com.florizt.base.repository.net.service
import com.florizt.base.repository.networkBoundResourceNoCache
import com.florizt.base.repository.networkBoundResourceOnlyCache
import com.florizt.base.ui.ImmersionBars
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.gyf.immersionbar.ImmersionBar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

@ImmersionBars
class MainActivity : AppCompatActivity() {

    private val binding by viewBinding<ActivityMainBinding>()
    private val viewModel by viewModelsLifecycle<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.vp.orientation = ORIENTATION_VERTICAL
        binding.vp.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int = 20

            override fun createFragment(position: Int): Fragment {
                return TestFragment.instance(position)
            }
        }

        binding.vp.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
            }

            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)

            }

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                println("------------ onPageSelected  $position")
            }
        })

        lifecycleScope.launch {
            request().flowOn(Dispatchers.IO)
                .collectLatest {
                    println("--------------- $it")
                }
        }
    }


    fun request(): Flow<Resource<String>> {
        return networkBoundResourceOnlyCache {
            flowOf(null)
        }
    }

}