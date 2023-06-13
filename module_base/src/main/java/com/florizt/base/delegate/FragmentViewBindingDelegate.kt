package com.florizt.base.delegate

import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import kotlinx.coroutines.launch
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * 委托方式懒加载Fragment#ViewBinding
 * 1、处理viewLifecycleOwner内存泄露
 * 2、DataBinding情况下绑定LiveData，不然xml收不到数据改变通知
 * @receiver Fragment
 * @return FragmentViewBindingDelegate<T>
 */
inline fun <reified VB : ViewBinding> Fragment.viewBinding() = object : ReadOnlyProperty<Fragment, VB> {
    private val clearBindingHandler by lazy(LazyThreadSafetyMode.NONE) { Handler(Looper.getMainLooper()) }
    private var binding: VB? = null

    init {
        //这里处理了ViewBinding在Fragment中的内存泄漏问题
        lifecycleScope.launch {
            viewLifecycleOwnerLiveData.observe(this@viewBinding) { viewLifecycleOwner ->
                viewLifecycleOwner.lifecycle.addObserver(object : DefaultLifecycleObserver {
                    override fun onDestroy(owner: LifecycleOwner) {
                        // Lifecycle listeners are called before onDestroyView in a Fragment.
                        // However, we want views to be able to use bindings in onDestroyView
                        // to do cleanup so we clear the reference one frame later.
                        clearBindingHandler.post { binding = null }
                    }
                })
            }
        }
    }

    override fun getValue(thisRef: Fragment, property: KProperty<*>): VB {
        if (binding != null && binding!!.root !== thisRef.view) {
            binding = null
        }
        return binding ?: run {
            val lifecycle = thisRef.viewLifecycleOwner.lifecycle
            if (!lifecycle.currentState.isAtLeast(Lifecycle.State.INITIALIZED)) {
                error("Cannot access view bindings. View lifecycle is ${lifecycle.currentState}!")
            }
            VB::class.java.getMethod("bind", View::class.java).run {
                invoke(null, thisRef.requireView()) as VB
            }.apply {
                if (this is ViewDataBinding) {
                    lifecycleOwner = thisRef
                }
            }
        }
    }
}


