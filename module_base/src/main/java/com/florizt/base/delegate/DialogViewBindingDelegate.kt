package com.florizt.base.delegate

import android.app.Dialog
import android.view.LayoutInflater
import androidx.viewbinding.ViewBinding
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * 委托方式懒加载Dialog#ViewBinding
 * 调用 [Dialog.setContentView]
 * @receiver Dialog
 * @return ReadOnlyProperty<Dialog, T>
 */
inline fun <reified VB : ViewBinding> Dialog.viewBinding(): ReadOnlyProperty<Dialog, VB> =
    object : ReadOnlyProperty<Dialog, VB> {
        private var binding: VB? = null

        override fun getValue(thisRef: Dialog, property: KProperty<*>): VB {
            return binding ?: run {
                VB::class.java.getMethod("inflate", LayoutInflater::class.java).run {
                    invoke(null, thisRef.layoutInflater) as VB
                }.apply {
                    binding = this
                    thisRef.setContentView(root)
                }
            }
        }
    }