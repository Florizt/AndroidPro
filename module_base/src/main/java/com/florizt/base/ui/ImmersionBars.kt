package com.florizt.base.ui

import android.app.Activity
import android.app.Application
import android.app.Application.ActivityLifecycleCallbacks
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.florizt.base.delegate.noOpDelegate
import com.florizt.base.ext.safe
import com.gyf.immersionbar.BarHide
import com.gyf.immersionbar.ktx.immersionBar

/**
 * 状态栏、导航栏、软键盘等一系列ui配置
 * @property isFullScreen Boolean 是否全屏
 * @property fitsSystemWindows Boolean 布局与状态栏是否重叠问题
 * @property statusBarDarkFont Boolean 状态栏字体是否深色
 * @property statusBarColor String 状态栏背景颜色
 * @property navigationBarColor String 底部导航栏背景颜色
 * @property hideNavigationBar Boolean 是否隐藏底部导航栏
 * @property keyboardEnable Boolean 解决EditText与软键盘冲突，true：EditText跟随软键盘弹起，false反之
 * @property keyboardMode Int 软键盘模式
 * @constructor
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class ImmersionBars(
    val isFullScreen: Boolean = false,
    val fitsSystemWindows: Boolean = true,
    val statusBarDarkFont: Boolean = true,
    val statusBarColor: String = "#ffffff",
    val navigationBarColor: String = "#00ffffff",
    val hideNavigationBar: Boolean = false,
    val keyboardEnable: Boolean = true,
    val keyboardMode: Int = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE,
)

/**
 * ImmersionBar观察者
 * @property onKeyboardChange Function2<[@kotlin.ParameterName] Boolean, [@kotlin.ParameterName] Int, Unit>
 * @constructor
 */
class ImmersionBarObserver(val activity: ComponentActivity) : DefaultLifecycleObserver {
    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        activity::class.java.annotations.forEach { anno ->
            if (anno is ImmersionBars) {
                activity.immersionBar {
                    if (anno.isFullScreen) {
                        hideBar(BarHide.FLAG_HIDE_BAR)
                        init()
                    } else {
                        fitsSystemWindows(anno.fitsSystemWindows)
                        statusBarDarkFont(anno.statusBarDarkFont, 0.2f)
                        statusBarColor(anno.statusBarColor)
                        navigationBarColor(anno.navigationBarColor).also {
                            if (anno.hideNavigationBar) {
                                it.hideBar(BarHide.FLAG_HIDE_NAVIGATION_BAR)
                            }
                        }
                        keyboardEnable(anno.keyboardEnable)
                        keyboardMode(anno.keyboardMode)
                        setOnKeyboardListener { isPopup, keyboardHeight ->
                            safe {
                                activity::class.java.getMethod(
                                    "onKeyboardChange",
                                    Boolean::class.java,
                                    Int::class.java
                                ).invoke(activity, isPopup, keyboardHeight)
                            }
                        }
                        init()
                    }
                }
                return@forEach
            }
        }
    }
}

/**
 * ImmersionBar初始化，生命周期绑定
 * @receiver LifecycleOwner
 * @param onKeyboardChange Function2<[@kotlin.ParameterName] Boolean, [@kotlin.ParameterName] Int, Unit>
 */
fun Application.initImmersionBar() {
    registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks by noOpDelegate() {
        override fun onActivityStarted(activity: Activity) {
            if (activity is ComponentActivity) {
                activity.lifecycle.addObserver(ImmersionBarObserver(activity))
            }
        }
    })
}

/**
 * 软键盘监听
 */
interface KeyboardChangeObserve {
    /**
     * 软键盘监听回调
     * @param isPopup Boolean 是否弹起
     * @param keyboardHeight Int 软键盘高度
     */
    fun onKeyboardChange(
        isPopup: Boolean,
        keyboardHeight: Int,
    )
}
