package com.florizt.base.delegate

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.florizt.base.ext.put
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * 委托方式Fragment传参
 * @param key String? 键，可不传，不传时默认为变量名
 * @return ReadWriteProperty<Fragment, T>
 */
@JvmOverloads
fun <V> argument(key: String? = null) = object : ReadWriteProperty<Fragment, V> {
    @Suppress("UNCHECKED_CAST")
    override fun getValue(
        thisRef: Fragment,
        property: KProperty<*>,
    ): V {
        val k = key ?: property.name
        return thisRef.arguments?.get(k) as? V
            ?: error("Property $k could not be read")
    }

    override fun setValue(
        thisRef: Fragment,
        property: KProperty<*>, value: V,
    ) {
        thisRef.arguments = (thisRef.arguments ?: Bundle().also(thisRef::setArguments)).apply {
            put(key ?: property.name, value)
        }
    }
}