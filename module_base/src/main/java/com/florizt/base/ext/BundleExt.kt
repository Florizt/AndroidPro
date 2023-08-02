@file:JvmName("BundleExt")

package com.florizt.base.ext

import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import androidx.annotation.RequiresApi
import java.io.Serializable

/**
 * Bundle扩展
 * @receiver Bundle
 * @param key String
 * @param value T
 */
inline fun <reified T> Bundle.put(key: String, value: T?) {
    when (T::class) {
        Boolean::class -> putBoolean(key, value as? Boolean ?: false)
        String::class -> putString(key, value as? String)
        Int::class -> putInt(key, value as? Int ?: 0)
        Short::class -> putShort(key, value as? Short ?: 0)
        Long::class -> putLong(key, value as? Long ?: 0L)
        Float::class -> putFloat(key, value as? Float ?: 0f)
        Byte::class -> putByte(key, value as? Byte ?: 0)
        ByteArray::class -> putByteArray(key, value as? ByteArray)
        Char::class -> putChar(key, value as? Char ?: ' ')
        CharArray::class -> putCharArray(key, value as? CharArray)
        CharSequence::class -> putCharSequence(key, value as? CharSequence)
        Bundle::class -> putBundle(key, value as? Bundle)
        Parcelable::class -> putParcelable(key, value as? Parcelable)
        Serializable::class -> putSerializable(key, value as? Serializable)
        else -> error("Type of property $key is not supported")
    }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
inline fun <reified T> Bundle.got(key: String): T? {
    return when (T::class) {
        Boolean::class -> getBoolean(key) as T
        String::class -> getString(key) as? T
        Int::class -> getInt(key) as T
        Short::class -> getShort(key) as T
        Long::class -> getLong(key) as T
        Float::class -> getFloat(key) as T
        Byte::class -> getByte(key) as T
        ByteArray::class -> getByteArray(key) as? T
        Char::class -> getChar(key) as T
        CharArray::class -> getCharArray(key) as? T
        CharSequence::class -> getCharSequence(key) as? T
        Bundle::class -> getBundle(key) as? T
        Parcelable::class -> getParcelable(key, T::class.java)
        Serializable::class -> getSerializable(key) as? T
        else -> error("Type of property $key is not supported")
    }
}