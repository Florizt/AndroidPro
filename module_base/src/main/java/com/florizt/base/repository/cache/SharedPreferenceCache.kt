package com.florizt.base.repository.cache

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.util.Base64
import com.florizt.base.app.ContextWrapper
import com.florizt.base.ext.decrypt3DES
import com.florizt.base.ext.encrypt3DES
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.Serializable
import java.lang.reflect.Method
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * 委托方式获取SharedPreferences，获取非Serializable类型
 * @param key String? 键，可不传，不传时默认为变量名
 * @param psw String? 加密密钥，默认3DES加密
 * @return ReadWriteProperty<Context, T>
 */
@JvmOverloads
inline fun <reified T> sharedPreference(key: String? = null, psw: String? = null) =
    object : ReadWriteProperty<Any, T?> {
        override fun getValue(thisRef: Any, property: KProperty<*>): T? {
            return ContextWrapper.context.getSharedPreferences().get<T>(
                key ?: property.name,
                psw
            )
        }

        override fun setValue(thisRef: Any, property: KProperty<*>, value: T?) {
            value?.apply {
                ContextWrapper.context.getSharedPreferences().put(
                    key ?: property.name,
                    this,
                    psw
                )
            }
        }
    }

/**
 * 委托方式获取SharedPreferences，获取Serializable类型
 * @param key String? 键，可不传，不传时默认为变量名
 * @param psw String? 加密密钥，默认3DES加密
 * @return ReadWriteProperty<Context, T>
 */
@JvmOverloads
inline fun <reified T : Serializable> sharedPreferenceSerializable(key: String? = null, psw: String? = null) =
    object : ReadWriteProperty<Any, T?> {
        override fun getValue(thisRef: Any, property: KProperty<*>): T? {
            return ContextWrapper.context.getSharedPreferences().getSerializable<T>(
                key ?: property.name,
                psw
            )
        }

        override fun setValue(thisRef: Any, property: KProperty<*>, value: T?) {
            value?.apply {
                ContextWrapper.context.getSharedPreferences().put(
                    key ?: property.name,
                    this,
                    psw
                )
            }
        }
    }

@Synchronized
fun Context.getSharedPreferences(): SharedPreferences =
    getSharedPreferences(packageName, Context.MODE_PRIVATE)

/**
 * 存储
 * @receiver SharedPreferences
 * @param key String
 * @param value Any
 * @param psw String? 加密密钥，默认3DES加密
 * @return Boolean 是否存储成功
 *
 * Serializable：将对象转化为字节流存储在外部设备，需要时重新生成对象(依靠反射)，因为使用反射，所以会产生大量的临时变量，从而引起频繁的GC；
 * Parcelable：整个过程都在内存中进行，反序列化读取的就是原对象，不会创建新对象。相比之下Parcelable性能更高，效率是Serializable的十倍以上。
 *  要注意的是：不能使用要将数据存储在磁盘上(比如永久性保存对象，或者保存对象的字节序列到本地文件中)，因为Parcel是为了更好的实现在IPC间传递对象，
 *            并不是一个通用的序列化机制，当改变任何Parcel中数据的底层实现都可能导致之前的数据不可读取
 *
 * 1、在内存中传输时更推荐Parcelable(比如在网络中传输对象或者进程间传输对象，还有Intent)
 * 2、数据持久化还是要使用Serializable(比如外部设备保存对象状态或者网络传输对象)
 */
@JvmOverloads
fun SharedPreferences.put(
    key: String,
    value: Any,
    psw: String? = null
): Boolean {
    val editor = edit()
    var bos: ByteArrayOutputStream? = null
    var oos: ObjectOutputStream? = null
    try {
        val newValue = when (value) {
            is String,
            is Long,
            is Boolean,
            is Float,
            is Int -> value.toString()

            is Serializable -> {
                bos = ByteArrayOutputStream()
                oos = ObjectOutputStream(bos)
                oos.writeObject(value)
                val bytes: ByteArray = bos.toByteArray()
                Base64.encodeToString(bytes, Base64.DEFAULT)
            }

            else -> throw IllegalArgumentException("SharedPreferences not support this type")
        }

        editor.putString(key, psw?.run {
            newValue.encrypt3DES(this)
        } ?: run {
            newValue
        })
        return true
    } catch (e: Throwable) {
        e.printStackTrace()
        return false
    } finally {
        SharedPreferencesCompat.apply(editor)
        try {
            bos?.close()
            oos?.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}

/**
 * 获取非Serializable类型
 * @receiver SharedPreferences
 * @param key String
 * @param psw String? 解密密钥，默认3DES加密
 * @return T 数据类型
 */
@JvmOverloads
inline fun <reified T> SharedPreferences.get(
    key: String,
    psw: String? = null
): T? {
    return try {
        getString(key, null)?.run {
            val newValue: String = psw?.let {
                this.decrypt3DES(it)
            } ?: run { this }
            when (T::class) {
                String::class -> newValue as T
                Long::class -> newValue.toLong() as T
                Boolean::class -> newValue.toBoolean() as T
                Float::class -> newValue.toFloat() as T
                Int::class -> newValue.toInt() as T
                else -> throw IllegalArgumentException("SharedPreferences not support this type")
            }
        } ?: run {
            getDefaultValue()
        }
    } catch (e: Throwable) {
        e.printStackTrace()
        getDefaultValue()
    }
}

/**
 * 获取Serializable类型
 * @receiver SharedPreferences
 * @param key String
 * @param psw String? 解密密钥，默认3DES加密
 * @return T 数据类型
 */
@JvmOverloads
inline fun <reified T : Serializable> SharedPreferences.getSerializable(
    key: String,
    psw: String? = null
): T? {
    var bis: ByteArrayInputStream? = null
    var ois: ObjectInputStream? = null
    return try {
        getString(key, null)?.run {
            val newValue: String = psw?.let {
                this.decrypt3DES(it)
            } ?: run { this }
            val bytes =
                Base64.decode(newValue, Base64.DEFAULT)
            bis = ByteArrayInputStream(bytes)
            ois = ObjectInputStream(bis)
            ois?.readObject() as T
        } ?: run {
            getDefaultValue()
        }
    } catch (e: Throwable) {
        e.printStackTrace()
        getDefaultValue()
    } finally {
        try {
            bis?.close()
            ois?.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}

/**
 * 获取默认值
 * @return T?
 */
inline fun <reified T> getDefaultValue(): T? {
    return when (T::class) {
        String::class -> "" as T
        Long::class -> 0L as T
        Boolean::class -> false as T
        Float::class -> 0f as T
        Int::class -> 0 as T
        else -> {
            null
        }
    }
}

/**
 * 移除某个key值已经对应的值
 */
@SuppressLint("CommitPrefEdits")
fun SharedPreferences.remove(key: String): Boolean {
    try {
        edit().apply {
            remove(key)
            SharedPreferencesCompat.apply(this)
        }
        return true
    } catch (e: Exception) {
        e.printStackTrace()
        return false
    }
}

/**
 * 清除所有数据
 */
@SuppressLint("CommitPrefEdits")
fun SharedPreferences.clear() {
    edit().apply {
        clear()
        SharedPreferencesCompat.apply(this)
    }
}

/**
 * 创建一个解决SharedPreferencesCompat.apply方法的一个兼容类
 */
private object SharedPreferencesCompat {
    private val sApplyMethod: Method? = findApplyMethod()

    /**
     * 反射查找apply的方法
     */
    private fun findApplyMethod(): Method? {
        try {
            val clz: Class<*> = SharedPreferences.Editor::class.java
            return clz.getMethod("apply")
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    /**
     * 如果找到则使用apply执行，否则使用commit
     */
    fun apply(editor: SharedPreferences.Editor) {
        try {
            if (sApplyMethod != null) {
                sApplyMethod.invoke(editor)
                return
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        editor.commit()
    }
}