package com.florizt.base.app

import android.app.Application

/**
 * 管理全局Context
 */
object ContextWrapper {
    private var _context: Application? = null
    val context: Application = _context ?: run {
        try {
            val application = Class.forName("android.app.ActivityThread")
                .getMethod("currentApplication")
                .invoke(null) as Application
            application.apply { _context = this }
        } catch (ex: Exception) {
            ex.printStackTrace()
            throw IllegalArgumentException("ActivityThread init failed")
        }
    }
}