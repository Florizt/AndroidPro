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
 *
 * onDestroyView()->onDestroy()
 * 在Fragment中使用时需要注意在onDestroyView()的时候把binding对象置空，
 * 因为Fragment的生命周期和Fragment中View的生命周期是不同步的；
 * 而binding绑定的是视图，当视图被销毁时，binding就不应该再被访问且能够被回收，
 * 因此，我们需要在onDestroyView()中将binding对象置空；
 * 否则，当视图被销毁时，Fragment继续持有binding的引用，就会导致binding无法被回收，造成内存泄漏。
 *
 * viewLifecycleOwner.onDestroy->onDestroyView()->onDestroy()
 * Fragment的viewLifecycleOwner会在Fragment的onDestroyView()之前执行onDestroy()。
 *
 * 通过在viewLifecycleOwner的onDestroy()时使用主线程Handler.post将binding置空的任务添加到消息队列中，
 * 而viewLifecycleOwner的onDestroy()和Fragment的onDestroyView()方法是在同一个消息中被处理的，即源码的performDestroyView()中，
 * 因此，post的Runnable自然会在onDestroyView()之后。
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
                    lifecycleOwner = thisRef.viewLifecycleOwner
                }
            }
        }
    }
}


