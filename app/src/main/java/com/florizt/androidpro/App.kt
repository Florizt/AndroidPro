package com.florizt.androidpro

import android.app.Application
import com.florizt.base.AndroidPro
import com.florizt.base.repository.net.entity.ApiResponse

/**
 * Created by wuwei
 * 2023/5/30
 * 佛祖保佑       永无BUG
 * desc：
 */
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        AndroidPro.with(this)
            .debug(BuildConfig.DEBUG)
            .initUIStyle()
            .initNetStyle<User<*>>(
                "https://www.baidu.com",
                "sasasa",
                {
                    ApiResponse(it.name, it.age, it.data)
                },
                arrayListOf()
            )
            .initCacheStyle()
    }
}

data class User<D>(
    val name: String,
    val age: String,
    val data:D?
)