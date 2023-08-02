@file:JvmName("ContextWrapper")

package com.florizt.base.app

import android.annotation.SuppressLint
import android.app.Application
import com.florizt.base.ext.safe

/**
 * 管理全局Context
 */
object ContextWrapper {
    private var _context: Application? = null
    val context: Application
        @SuppressLint("PrivateApi")
        get() = _context ?: run {
            safe(
                block = {
                    (Class.forName("android.app.ActivityThread")
                        .getMethod("currentApplication")
                        .invoke(null) as Application).apply { _context = this }
                },
                error = { error("ActivityThread init failed") })
        }
}