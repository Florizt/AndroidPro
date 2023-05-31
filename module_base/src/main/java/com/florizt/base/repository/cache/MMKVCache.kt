package com.florizt.base.repository.cache

import android.os.Parcelable
import com.tencent.mmkv.MMKV
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * 委托方式获取mmkv，获取非Parcelable类型
 * @param key String? 键
 * @param mode Int 单进程还是多进程，默认单进程
 * @param cryptKey String? 加密密钥
 * @return ReadWriteProperty<Any, T?>
 */
@JvmOverloads
inline fun <reified T> mmkv(
    key: String? = null,
    mode: Int = MMKV.SINGLE_PROCESS_MODE,
    cryptKey: String? = null
) = object : ReadWriteProperty<Any, T?> {
    val mmkv = MMKV.defaultMMKV(mode, cryptKey)
    override fun getValue(thisRef: Any, property: KProperty<*>): T? {
        return mmkv.get(key ?: property.name)
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: T?) {
        value?.apply {
            mmkv.put(key ?: property.name, this)
        }
    }
}

/**
 * 委托方式获取mmkv，获取Parcelable类型
 * @param key String? 键
 * @param mode Int 单进程还是多进程，默认单进程
 * @param cryptKey String? 加密密钥
 * @return ReadWriteProperty<Any, T?>
 */
@JvmOverloads
inline fun <reified T : Parcelable> mmkvParcelable(
    key: String? = null,
    mode: Int = MMKV.SINGLE_PROCESS_MODE,
    cryptKey: String? = null
) = object : ReadWriteProperty<Any, T?> {
    val mmkv = MMKV.defaultMMKV(mode, cryptKey)
    override fun getValue(thisRef: Any, property: KProperty<*>): T? {
        return mmkv.getParcelable(key ?: property.name)
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: T?) {
        value?.apply {
            mmkv.put(key ?: property.name, this)
        }
    }
}

/**
 * 存储
 * @receiver MMKV
 * @param key String 键
 * @param value Any 值
 * @return Boolean
 */
fun MMKV.put(key: String, value: Any) {
    when (value) {
        is String -> encode(key, value)
        is Long -> encode(key, value)
        is Boolean -> encode(key, value)
        is Float -> encode(key, value)
        is Int -> encode(key, value)
        is Double -> encode(key, value)
        is ByteArray -> encode(key, value)
        is Parcelable -> encode(key, value)
        else -> throw IllegalArgumentException("mmkv not support this type")
    }
}

/**
 * 获取非Parcelable类型
 * @receiver MMKV
 * @param key String
 * @return T?
 */
inline fun <reified T> MMKV.get(key: String): T? {
    return when (T::class) {
        String::class -> decodeString(key) as T
        Long::class -> decodeLong(key) as T
        Boolean::class -> decodeBool(key) as T
        Float::class -> decodeFloat(key) as T
        Int::class -> decodeInt(key) as T
        Double::class -> decodeDouble(key) as T
        ByteArray::class -> decodeBytes(key) as T
        else -> throw IllegalArgumentException("mmkv not support this type")
    }
}

/**
 * 获取Parcelable类型
 * @receiver MMKV
 * @param key String
 * @return T?
 */
inline fun <reified T : Parcelable> MMKV.getParcelable(key: String): T? {
    return decodeParcelable(key, T::class.java)
}

fun MMKV.removeKey(key: String) {
    removeValueForKey(key)
}