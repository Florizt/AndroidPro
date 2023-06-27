package com.florizt.androidpro

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import coil.load
import coil.transform.CircleCropTransformation
import com.florizt.androidpro.databinding.ActivityMainBinding
import com.florizt.base.delegate.viewBinding
import com.florizt.base.delegate.viewModelsLifecycle
import com.florizt.base.ext.clickFlow
import com.florizt.base.ext.textChangeFlow
import com.florizt.base.ext.toast
import com.florizt.base.ui.ImmersionBars
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectIndexed
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.fold
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onEmpty
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.sample
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.flow.transformLatest
import kotlinx.coroutines.launch

@ImmersionBars
class MainActivity : AppCompatActivity() {

    private val binding by viewBinding<ActivityMainBinding>()
    private val viewModel by viewModelsLifecycle<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.iv.load(R.mipmap.ic_launcher) {
            placeholder(R.drawable.ic_launcher_background)
            transformations(CircleCropTransformation())
        }

        binding.et.textChangeFlow(
            scope = lifecycleScope,
            fetch = {
                //模拟网络请求加载数据并发射
                flow { emit(1) }
            },
            result = {

            }
        )

        binding.iv.clickFlow(scope = lifecycleScope) {

        }

        lifecycleScope.launch {
            flow {
                while (true) {
                    emit("发送一条弹幕")
                }
            }.distinctUntilChangedBy {

            }.map {
                1
            }
                .sample(1000)
                .flowOn(Dispatchers.IO)
                .collect {
                    println(it)
                }
        }


    }
}

