@file:JvmName("AppLogger")

package com.florizt.base.app

import android.util.Log
import com.dgrlucky.log.LogX

/**
 * 日志
 */
object AppLogger {
    private var debug = true

    @JvmStatic
    fun init(debug: Boolean) {
        AppLogger.debug = debug
    }

    enum class LEVEL {
        V, D, I, W, E, NORMAL
    }

    @JvmStatic
    fun log(level: LEVEL, msg: String) {
        if (!debug) return
        when (level) {
            LEVEL.V -> {
                LogX.v(msg)
            }

            LEVEL.D -> {
                LogX.d(msg)
            }

            LEVEL.I -> {
                LogX.i(msg)
            }

            LEVEL.W -> {
                LogX.w(msg)
            }

            LEVEL.E -> {
                LogX.e(msg)
            }

            LEVEL.NORMAL -> {
                Log.v("AppLogger", msg)
            }
        }
    }
}