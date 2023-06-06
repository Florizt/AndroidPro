package com.florizt.base.app

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
        V, D, I, W, E
    }

    @JvmStatic
    fun log(level: LEVEL, any: Any) {
        if (!debug) return
        when (level) {
            LEVEL.V -> {
                LogX.v(any)
            }

            LEVEL.D -> {
                LogX.d(any)
            }

            LEVEL.I -> {
                LogX.i(any)
            }

            LEVEL.W -> {
                LogX.w(any)
            }

            LEVEL.E -> {
                LogX.e(any)
            }

            else -> {
                LogX.s(any)
            }
        }
    }

    @JvmStatic
    fun log(level: LEVEL, msg: String, vararg args: Any) {
        if (!debug) return
        when (level) {
            LEVEL.V -> {
                LogX.v(msg, args)
            }

            LEVEL.D -> {
                LogX.d(msg, args)
            }

            LEVEL.I -> {
                LogX.i(msg, args)
            }

            LEVEL.W -> {
                LogX.w(msg, args)
            }

            LEVEL.E -> {
                LogX.e(msg, args)
            }

            else -> {
                LogX.s(msg, args)
            }
        }
    }
}