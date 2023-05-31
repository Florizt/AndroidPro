package com.florizt.base.delegate

import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ViewModel
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * 委托方式懒加载ViewModel
 * 1、如果ViewModel实现了LifecycleObserver，则将ViewModel绑定lifecycle生命周期
 * @receiver ComponentActivity
 * @return ReadOnlyProperty<ComponentActivity, VM>
 */
inline fun <reified VM : ViewModel> ComponentActivity.viewModelsLifecycle() =
    object : ReadOnlyProperty<ComponentActivity, VM> {
        private var viewmodel: VM? = null

        override fun getValue(thisRef: ComponentActivity, property: KProperty<*>): VM {
            return viewmodel?.run {
                this
            } ?: run {
                viewModels<VM>().run {
                    value.apply {
                        viewmodel = this
                        if (this is LifecycleObserver) {
                            lifecycle.addObserver(this)
                        }
                    }
                }
            }
        }
    }