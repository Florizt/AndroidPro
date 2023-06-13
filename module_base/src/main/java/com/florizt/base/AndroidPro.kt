package com.florizt.base

import android.app.Application
import com.florizt.base.app.AppLogger
import com.florizt.base.ui.AppStackManager.initAppStack
import com.florizt.base.ui.initImmersionBar
import com.florizt.base.ui.initScreenAdapter
import com.florizt.base.repository.net.RetrofitFactory
import com.florizt.base.repository.net.entity.ApiResponse
import com.tencent.mmkv.MMKV
import okhttp3.Interceptor

/**
 * AndroidPro框架初始化
 */
object AndroidPro {
    private var application: Application? = null

    fun Application.androidPro(block: AndroidPro.() -> Unit) {
        with(this).apply { block() }
    }

    /**
     * 初始化Application
     * @param application Application
     * @return AndroidPro
     */
    @JvmStatic
    private fun with(application: Application): AndroidPro {
        this.application = application
        return this
    }

    /**
     * 是否debug模式，影响日志输出
     * @param debug Boolean
     * @return AndroidPro
     */
    fun debug(debug: Boolean): AndroidPro {
        AppLogger.init(debug)
        return this
    }

    /**
     * 初始化UI相关配置，包括：适配、应用Activity栈管理、前后台管理、状态栏、导航栏、软键盘配置
     * @param designWidthInDp Int 全局设计图宽度
     * @param designHeightInDp Int 全局设计图高度
     * @param toForeground Function0<Unit> 应用回到前台
     * @param toBackground Function0<Unit> 应用退到后台
     * @return AndroidPro
     */
    @JvmStatic
    @JvmOverloads
    fun initUIStyle(
        designWidthInDp: Int = 1080,
        designHeightInDp: Int = 1920,
        toForeground: () -> Unit = {},
        toBackground: () -> Unit = {},
    ): AndroidPro {
        application?.apply {
            initScreenAdapter(designWidthInDp, designHeightInDp)
            initAppStack(toForeground, toBackground)
            initImmersionBar()
        } ?: error("first step is use with()")
        return this
    }

    /**
     * 初始化网络请求相关配置
     * @param baseUrl String 网络请求域名
     * @param successCode String 网络请求正常code
     * @param mapper Function1<T, ApiResponse<*>> 网络请求响应体和封装的ApiResponse映射函数
     * @param interceptors List<Interceptor> 网络请求拦截器
     * @return AndroidPro
     */
    @JvmStatic
    fun <T> initNetStyle(
        baseUrl: String,
        successCode: String,
        mapper: (T) -> ApiResponse<*>,
        interceptors: List<Interceptor>,
    ): AndroidPro {
        RetrofitFactory.initRetrofit<T>(
            baseUrl, successCode, mapper, interceptors
        )
        return this
    }

    /**
     * 初始化本地缓存相关配置
     * @param mmkvRootDir String mmkv缓存路径
     * @return AndroidPro
     */
    @JvmStatic
    @JvmOverloads
    fun initCacheStyle(
        mmkvRootDir: String = "",
    ): AndroidPro {
        application?.apply {
            MMKV.initialize(this, mmkvRootDir)
        } ?: error("first step is use with()")
        return this
    }
}