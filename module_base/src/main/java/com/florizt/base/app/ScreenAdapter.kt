package com.florizt.base.app

import android.app.Application
import me.jessyan.autosize.AutoSizeConfig

/**
 * 在Application中使用，配置应用适配尺寸
 * @property designWidthInDp Int
 * @property designHeightInDp Int
 * @constructor
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class ScreenAdapter(
    val designWidthInDp: Int = 960,
    val designHeightInDp: Int = 1280
)

/**
 * 屏幕适配初始化
 * @receiver Application
 */
fun Application.initScreenAdapter() {
    this::class.java.annotations.forEach { anno ->
        if (anno is ScreenAdapter) {
            AutoSizeConfig.getInstance().setDesignWidthInDp(anno.designWidthInDp)
            AutoSizeConfig.getInstance().setDesignHeightInDp(anno.designHeightInDp)
            AutoSizeConfig.getInstance().setExcludeFontScale(true)
            return@forEach
        }
    }
}