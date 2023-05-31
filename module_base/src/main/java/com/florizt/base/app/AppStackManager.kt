package com.florizt.base.app

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.florizt.base.delegate.noOpDelegate
import java.lang.ref.SoftReference
import java.util.*

/**
 * Activity堆栈管理类，包括应用进入前后台管理
 * @property appCount Int
 * @property runInBackground Boolean
 * @property activityStack SoftReference<Stack<Activity>>
 * @property fragmentStack SoftReference<Stack<Fragment>>
 */
class AppStackManager private constructor() {
    companion object {
        val instance: AppStackManager by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            AppStackManager()
        }
    }

    private var appCount: Int = 0
    private var runInBackground: Boolean = true

    private val activityStack: SoftReference<Stack<Activity>> = SoftReference(Stack<Activity>())
    private val fragmentStack: SoftReference<Stack<Fragment>> = SoftReference(Stack<Fragment>())

    fun getAppCount() = appCount

    fun setAppCount(count: Int) {
        appCount = count
    }

    fun getRunInBackground() = runInBackground

    fun setRunInBackground(background: Boolean) {
        runInBackground = background
    }

    /**
     * 添加Activity到堆栈
     */
    fun addActivity(activity: Activity) {
        activityStack.get()?.also { it.add(activity) }
    }

    /**
     * 移除指定的Activity
     */
    fun removeActivity(activity: Activity) {
        activityStack.get()?.also { it.remove(activity) }
    }


    /**
     * 是否有activity
     */
    fun hasActivity(): Boolean {
        return activityStack.get()?.let { if (it.isEmpty()) false else true } ?: false
    }

    /**
     * 获取当前Activity（堆栈中最后一个压入的）
     */
    fun currentActivity(): Activity? {
        return activityStack.get()?.lastElement()
    }

    /**
     * 结束当前Activity（堆栈中最后一个压入的）
     */
    fun finishActivity() {
        activityStack.get()?.let { finishActivity(it.lastElement()) }
    }

    /**
     * 结束指定的Activity
     */
    fun finishActivity(activity: Activity) {
        if (!activity.isFinishing) {
            activity.finish()
        }
        removeActivity(activity)
    }

    /**
     * 结束指定类名的Activity
     */
    fun finishActivity(cls: Class<*>) {
        activityStack.get()?.let {
            for (activity in it) {
                if (activity.javaClass == cls) {
                    finishActivity(activity)
                    break
                }
            }
        }
    }

    /**
     * 结束所有Activity
     */
    fun finishAllActivity() {
        activityStack.get()?.let {
            it.forEach {
                if (!it.isFinishing) {
                    it.finish()
                }
            }
            it.clear()
        }
    }

    /**
     * 获取指定的Activity
     */
    fun getActivity(cls: Class<*>): Activity? {
        activityStack.get()?.let {
            for (activity in it) {
                if (activity.javaClass == cls) {
                    return activity
                }
            }
        }
        return null
    }


    /**
     * 添加Fragment到堆栈
     */
    fun addFragment(fragment: Fragment) {
        fragmentStack.get()?.also { it.add(fragment) }
    }

    /**
     * 移除指定的Fragment
     */
    fun removeFragment(fragment: Fragment) {
        fragmentStack.get()?.also { it.remove(fragment) }
    }


    /**
     * 是否有Fragment
     */
    fun hasFragment(): Boolean {
        return fragmentStack.get()?.let { if (it.isEmpty()) false else true } ?: false
    }

    /**
     * 获取当前Fragment（堆栈中最后一个压入的）
     */
    fun currentFragment(): Fragment? {
        return fragmentStack.get()?.lastElement()
    }

    /**
     * 退出应用程序
     */
    fun appExit() {
        try {
            finishAllActivity()
        } catch (e: Exception) {
            activityStack.get()?.also { it.clear() }
            e.printStackTrace()
        }
    }
}

/**
 * 注册全局Activity生命周期，管理堆栈
 * @receiver Application
 */
fun Application.initAppStack() {
    registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks by noOpDelegate() {
        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
            AppStackManager.instance.addActivity(activity)
        }

        override fun onActivityStarted(activity: Activity) {
            AppStackManager.instance.setAppCount(AppStackManager.instance.getAppCount() + 1)
            if (AppStackManager.instance.getRunInBackground()) {
                AppStackManager.instance.setRunInBackground(false)
            }
        }

        override fun onActivityPaused(activity: Activity) {
            if (activity.isFinishing()) {
                AppStackManager.instance.removeActivity(activity)
            }
        }

        override fun onActivityStopped(activity: Activity) {
            AppStackManager.instance.setAppCount(AppStackManager.instance.getAppCount() - 1)
            if (AppStackManager.instance.getAppCount() == 0) {
                AppStackManager.instance.setRunInBackground(true)
            }
        }
    })
}