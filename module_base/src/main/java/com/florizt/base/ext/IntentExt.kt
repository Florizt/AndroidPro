@file:JvmName("IntentExt")

package com.florizt.base.ext

import android.content.Intent
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
inline fun <reified T> Intent.put(key: String, value: T?) {
    when (T::class) {
        Boolean::class -> putExtra(key, value as? Boolean ?: false)
        String::class -> putExtra(key, value as? String)
        Int::class -> putExtra(key, value as? Int ?: 0)
        Short::class -> putExtra(key, value as? Short ?: 0)
        Long::class -> putExtra(key, value as? Long ?: 0L)
        Float::class -> putExtra(key, value as? Float ?: 0f)
        Byte::class -> putExtra(key, value as? Byte ?: 0)
        ByteArray::class -> putExtra(key, value as? ByteArray)
        Char::class -> putExtra(key, value as? Char ?: ' ')
        CharArray::class -> putExtra(key, value as? CharArray)
        CharSequence::class -> putExtra(key, value as? CharSequence)
        Bundle::class -> putExtra(key, value as? Bundle)
        Parcelable::class -> putExtra(key, value as? Parcelable)
        Serializable::class -> putExtra(key, value as? Serializable)
        else -> error("Type of property $key is not supported")
    }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
inline fun <reified T> Intent.got(key: String): T? {
    return when (T::class) {
        Boolean::class -> getBooleanExtra(key, false) as T
        String::class -> getStringExtra(key) as? T
        Int::class -> getIntExtra(key, 0) as T
        Short::class -> getShortExtra(key, 0) as T
        Long::class -> getLongExtra(key, 0L) as T
        Float::class -> getFloatExtra(key, 0f) as T
        Byte::class -> getByteExtra(key, 0) as T
        ByteArray::class -> getByteArrayExtra(key) as? T
        Char::class -> getCharExtra(key, ' ') as T
        CharArray::class -> getCharArrayExtra(key) as? T
        CharSequence::class -> getCharSequenceExtra(key) as? T
        Bundle::class -> getBundleExtra(key) as? T
        Parcelable::class -> getParcelableExtra(key, T::class.java)
        Serializable::class -> getSerializableExtra(key) as? T
        else -> error("Type of property $key is not supported")
    }
}