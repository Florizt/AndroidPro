package com.florizt.base.delegate

import android.app.Activity
import android.os.Bundle
import com.florizt.base.ext.got
import com.florizt.base.ext.put
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * 委托方式Activity传参
 * @param key String? 键，可不传，不传时默认为变量名
 * @return ReadWriteProperty<Fragment, T>
 */
@JvmOverloads
inline fun <reified V> intent(key: String? = null) = object : ReadWriteProperty<Activity, V?> {

    override fun getValue(
        thisRef: Activity,
        property: KProperty<*>,
    ): V? {
        return thisRef.intent.extras?.got(key ?: property.name)
    }

    override fun setValue(thisRef: Activity, property: KProperty<*>, value: V?) {
        thisRef.intent.putExtras((thisRef.intent.extras ?: Bundle()).apply {
            put(key ?: property.name, value)
        })
    }
}