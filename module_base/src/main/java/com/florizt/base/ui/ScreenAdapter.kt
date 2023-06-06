package com.florizt.base.ui

import android.app.Application
import me.jessyan.autosize.AutoSizeConfig

/**
 * 屏幕适配初始化
 * @receiver Application
 */
@JvmOverloads
fun Application.initScreenAdapter(
    designWidthInDp: Int = 1080,
    designHeightInDp: Int = 1920
) {
    AutoSizeConfig.getInstance().setDesignWidthInDp(designWidthInDp)
    AutoSizeConfig.getInstance().setDesignHeightInDp(designHeightInDp)
    AutoSizeConfig.getInstance().setExcludeFontScale(true)
}