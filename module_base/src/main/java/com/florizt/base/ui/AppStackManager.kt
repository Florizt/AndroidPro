package com.florizt.base.ui

import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.florizt.base.delegate.noOpDelegate
import com.florizt.base.ext.safe
import java.lang.ref.SoftReference
import java.util.*

/**
 * Activity堆栈管理类，包括应用进入前后台管理
 * @property appCount Int
 * @property runInBackground Boolean
 * @property activityStack SoftReference<Stack<Activity>>
 */
object AppStackManager {

    private var _appCount: Int = 0
    val appCount
        get() = _appCount

    private var _runInBackground: Boolean = true
    val runInBackground
        get() = _runInBackground

    private val activityStack: SoftReference<Stack<Activity>> = SoftReference(Stack<Activity>())

    /**
     * 添加Activity到堆栈
     */
    @JvmStatic
    fun addActivity(activity: Activity) {
        activityStack.get()?.add(activity)
    }

    /**
     * 移除指定的Activity
     */
    @JvmStatic
    fun removeActivity(activity: Activity) {
        activityStack.get()?.remove(activity)
    }


    /**
     * 是否有activity
     */
    @JvmStatic
    fun hasActivity(): Boolean {
        return activityStack.get()?.isNotEmpty() ?: false
    }

    /**
     * 获取当前Activity（堆栈中最后一个压入的）
     */
    @JvmStatic
    fun currentActivity(): Activity? {
        return activityStack.get()?.lastElement()
    }

    /**
     * 结束当前Activity（堆栈中最后一个压入的）
     */
    @JvmStatic
    fun finishActivity() {
        currentActivity()?.let { finishActivity(it) }
    }

    /**
     * 结束指定的Activity
     */
    @JvmStatic
    fun finishActivity(activity: Activity) {
        if (!activity.isFinishing) {
            activity.finish()
        }
        removeActivity(activity)
    }

    /**
     * 结束所有Activity
     */
    @JvmStatic
    fun finishAllActivity() {
        activityStack.get()?.run {
            forEach {
                if (!it.isFinishing) {
                    it.finish()
                }
            }
            clear()
        }
    }

    /**
     * 退出应用程序
     */
    @JvmStatic
    fun appExit() {
        safe(
            block = {
                finishAllActivity()
            },
            error = {
                activityStack.get()?.clear()
            }
        )
    }

    /**
     * 注册全局Activity生命周期，管理堆栈
     * @receiver Application
     */
    @JvmStatic
    @JvmOverloads
    fun Application.initAppStack(
        toForeground: () -> Unit = {},
        toBackground: () -> Unit = {},
    ) {
        registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks by noOpDelegate() {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                addActivity(activity)
            }

            override fun onActivityStarted(activity: Activity) {
                _appCount++
                if (_runInBackground) {
                    _runInBackground = false
                    toForeground()
                }
            }

            override fun onActivityPaused(activity: Activity) {
                if (activity.isFinishing()) {
                    removeActivity(activity)
                }
            }

            override fun onActivityStopped(activity: Activity) {
                _appCount--
                if (!_runInBackground) {
                    _runInBackground = true
                    toBackground()
                }
            }
        })
    }
}