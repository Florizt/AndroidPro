package com.florizt.base.delegate

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * 委托方式懒加载ViewGroup布局
 * @receiver ViewGroup
 * @return ReadOnlyProperty<ViewGroup, T>
 */
inline fun <reified VB : ViewBinding> ViewGroup.viewBinding() =
    object : ReadOnlyProperty<ViewGroup, VB> {
        private var binding: VB? = null

        override fun getValue(thisRef: ViewGroup, property: KProperty<*>): VB {
            return binding?.run {
                this
            } ?: run {
                VB::class.java.getMethod(
                    "inflate",
                    LayoutInflater::class.java,
                    ViewGroup::class.java,
                    Boolean::class.java
                ).run {
                    invoke(
                        null,
                        LayoutInflater.from(thisRef.context),
                        thisRef,
                        true
                    ) as VB
                }
            }
        }
    }


