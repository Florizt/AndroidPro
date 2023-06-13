package com.florizt.base.ext

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.florizt.base.app.ContextWrapper

/**
 * 获取color
 */
val Int.color
    get() = ContextCompat.getColor(ContextWrapper.context, this)

/**
 * 获取drawable
 */
val Int.drawable
    get() = ContextCompat.getDrawable(ContextWrapper.context, this)

/**
 * 获取string
 */
val Int.string
    get() = ContextWrapper.context.getString(this)

/**
 * 获取string
 */
fun Int.string(vararg formatArgs: String) =
    ContextWrapper.context.getString(this, formatArgs)

/**
 * 启动Activity
 */
inline fun <reified AC : Activity> Context.startActivity(
    args: Intent.() -> Unit,
) {
    startActivity(Intent(this, AC::class.java).apply {
        args()
    })
}

/**
 * 安全执行
 * @param block Function0<Unit>
 */
inline fun safe(block: () -> Unit) {
    try {
        block()
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

/**
 * 安全执行
 * @param block Function0<Unit>
 */
@JvmOverloads
inline fun safe(block: () -> Unit, finally: () -> Unit = {}) {
    try {
        block()
    } catch (e: Exception) {
        e.printStackTrace()
    } finally {
        finally()
    }
}

/**
 * 安全执行
 * @param block Function0<Unit>
 */
inline fun <T> safe(block: () -> T, error: (Exception) -> T): T {
    try {
        return block()
    } catch (e: Exception) {
        e.printStackTrace()
        return error(e)
    }
}

/**
 * 安全执行
 * @param block Function0<Unit>
 */
@JvmOverloads
inline fun <T> safe(block: () -> T, error: (Exception) -> T, finally: () -> Unit = {}): T {
    try {
        return block()
    } catch (e: Exception) {
        e.printStackTrace()
        return error(e)
    } finally {
        finally()
    }
}