package com.florizt.base.delegate

import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ViewModel
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * Activity委托方式懒加载ViewModel
 * 1、如果ViewModel实现了LifecycleObserver，则将ViewModel绑定lifecycle生命周期
 * @receiver ComponentActivity
 * @return ReadOnlyProperty<ComponentActivity, VM>
 */
inline fun <reified VM : ViewModel> ComponentActivity.viewModelsLifecycle() =
    object : ReadOnlyProperty<ComponentActivity, VM> {
        private var viewmodel: VM? = null

        override fun getValue(thisRef: ComponentActivity, property: KProperty<*>): VM {
            return viewmodel ?: run {
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

/**
 * Fragment委托方式懒加载ViewModel
 * 1、如果ViewModel实现了LifecycleObserver，则将ViewModel绑定lifecycle生命周期
 * @receiver Fragment
 * @return ReadOnlyProperty<Fragment, VM>
 */
inline fun <reified VM : ViewModel> Fragment.viewModelsLifecycle() =
    object : ReadOnlyProperty<Fragment, VM> {
        private var viewmodel: VM? = null

        override fun getValue(thisRef: Fragment, property: KProperty<*>): VM {
            return viewmodel ?: run {
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

/**
 * Fragment委托方式懒加载Activity#ViewModel
 * 1、如果ViewModel实现了LifecycleObserver，则将ViewModel绑定lifecycle生命周期
 * @receiver Fragment
 * @return ReadOnlyProperty<Fragment, VM>
 */
inline fun <reified VM : ViewModel> Fragment.activityViewModelsLifecycle() =
    object : ReadOnlyProperty<Fragment, VM> {
        private var viewmodel: VM? = null

        override fun getValue(thisRef: Fragment, property: KProperty<*>): VM {
            return viewmodel ?: run {
                activityViewModels<VM>().run {
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