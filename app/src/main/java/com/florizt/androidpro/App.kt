package com.florizt.androidpro

import android.app.Application
import com.florizt.base.app.ScreenAdapter
import com.florizt.base.app.initScreenAdapter
import com.florizt.base.app.initAppStack
import com.tencent.mmkv.MMKV

/**
 * Created by wuwei
 * 2023/5/30
 * 佛祖保佑       永无BUG
 * desc：
 */
@ScreenAdapter
class App: Application() {
    override fun onCreate() {
        super.onCreate()
        MMKV.initialize(this)
        initScreenAdapter()
        initAppStack()
    }
}