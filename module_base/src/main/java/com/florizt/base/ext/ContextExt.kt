@file:JvmName("ContextExt")

package com.florizt.base.ext

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Looper
import android.widget.Toast
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
@JvmOverloads
inline fun <reified AC : Activity> Context.startActivity(
    args: Intent.() -> Unit = {},
) {
    startActivity(Intent(this, AC::class.java).apply {
        args()
    })
}

/**
 * 任意线程toast
 * @param content String
 */
fun toast(content: String) {
    Looper.myLooper() ?: run {
        Looper.prepare()
    }
    Toast.makeText(ContextWrapper.context, content, Toast.LENGTH_SHORT).show()
    if (Thread.currentThread() != Looper.getMainLooper().thread) {
        Looper.loop()
        Looper.myLooper()?.quit()
    }
}