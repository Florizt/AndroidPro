package com.florizt.base.delegate

import java.lang.reflect.InvocationHandler
import java.lang.reflect.Proxy

/**
 * 通过委托实现接口默认方法
 * @return T
 */
inline fun <reified T : Any> noOpDelegate(): T {
    val javaClass = T::class.java
    val noDelegate = InvocationHandler { _, _, _ -> }
    return Proxy.newProxyInstance(javaClass.classLoader, arrayOf(javaClass), noDelegate) as T
}