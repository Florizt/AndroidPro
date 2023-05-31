package com.florizt.base.delegate

import android.app.Activity
import android.view.LayoutInflater
import androidx.activity.ComponentActivity
import androidx.databinding.ViewDataBinding
import androidx.viewbinding.ViewBinding
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * 委托方式懒加载Activity#ViewBinding
 * 1、调用 [Activity.setContentView]
 * 2、DataBinding情况下绑定LiveData，不然xml收不到数据改变通知
 * @receiver ComponentActivity
 * @return ReadOnlyProperty<ComponentActivity, T>
 */
inline fun <reified VB : ViewBinding> ComponentActivity.viewBinding(): ReadOnlyProperty<ComponentActivity, Any> =
    object : ReadOnlyProperty<ComponentActivity, VB> {
        private var binding: VB? = null

        override fun getValue(thisRef: ComponentActivity, property: KProperty<*>): VB {
            return binding?.run {
                this
            } ?: run {
                VB::class.java.getMethod("inflate", LayoutInflater::class.java).run {
                    invoke(null, thisRef.layoutInflater) as VB
                }.apply {
                    binding = this
                    thisRef.setContentView(root)
                    if (this is ViewDataBinding) {
                        lifecycleOwner = thisRef
                    }
                }
            }
        }
    }