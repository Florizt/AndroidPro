@file:JvmName("FlowBus")

package com.florizt.base.app

import androidx.lifecycle.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

/**
 * FlowBus消息总线
 */
object FlowBus {
    private val busMap = mutableMapOf<String, EventBus<*>>()
    private val busStickMap = mutableMapOf<String, StickEventBus<*>>()

    @Suppress("UNCHECKED_CAST")
    @Synchronized
    fun <T> with(key: String): EventBus<T> {
        return (busMap[key] ?: run {
            EventBus<T>(key).apply {
                busMap[key] = this
            }
        }) as EventBus<T>
    }

    @Suppress("UNCHECKED_CAST")
    @Synchronized
    fun <T> withStick(key: String): StickEventBus<T> {
        return (busStickMap[key] ?: run {
            StickEventBus<T>(key).apply {
                busStickMap[key] = this
            }
        }) as StickEventBus<T>
    }

    //真正实现类
    open class EventBus<T>(private val key: String) : DefaultLifecycleObserver {

        //私有对象用于发送消息
        protected val _events: MutableSharedFlow<T> by lazy {
            obtainEvent()
        }

        //暴露的公有对象用于接收消息
        val events = _events.asSharedFlow()

        open fun obtainEvent(): MutableSharedFlow<T> =
            MutableSharedFlow(0, 1, BufferOverflow.DROP_OLDEST)

        //主线程接收数据
        fun regist(lifecycleOwner: LifecycleOwner, action: (T) -> Unit) {
            lifecycleOwner.lifecycle.addObserver(this)
            lifecycleOwner.lifecycleScope.launch {
                events.collect { action(it) }
            }
        }

        //协程中发送数据
        suspend fun post(event: T) {
            _events.emit(event)
        }

        //主线程发送数据
        fun post(scope: CoroutineScope, event: T) {
            scope.launch {
                _events.emit(event)
            }
        }

        //自动销毁
        override fun onDestroy(owner: LifecycleOwner) {
            super.onDestroy(owner)
            if (_events.subscriptionCount.value <= 0)
                busMap.remove(key)
        }
    }

    class StickEventBus<T>(private val key: String) : EventBus<T>(key) {
        override fun obtainEvent(): MutableSharedFlow<T> =
            MutableSharedFlow(1, 1, BufferOverflow.DROP_OLDEST)

        override fun onDestroy(owner: LifecycleOwner) {
            if (_events.subscriptionCount.value <= 0)
                busStickMap.remove(key)
        }
    }

}