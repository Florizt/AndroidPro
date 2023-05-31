package com.florizt.base.app

import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.gyf.barlibrary.BarHide
import com.gyf.barlibrary.ImmersionBar

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
    val keyboardMode: Int = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
)

/**
 * ImmersionBar观察者
 * @property onKeyboardChange Function2<[@kotlin.ParameterName] Boolean, [@kotlin.ParameterName] Int, Unit>
 * @constructor
 */
class ImmersionBarObserver(
    val onKeyboardChange: (
        isPopup: Boolean,
        keyboardHeight: Int
    ) -> Unit = { _, _ -> }
) : DefaultLifecycleObserver {
    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        owner::class.java.annotations.forEach { anno ->
            if (anno is ImmersionBars) {
                val bar = if (owner is ComponentActivity)
                    owner.immersionBar()
                else if (owner is Fragment)
                    owner.immersionBar()
                else throw IllegalArgumentException("ImmersionBar must be used in ComponentActivity or Fragment")
                bar.apply {
                    if (anno.isFullScreen) {
                        hideBar(BarHide.FLAG_HIDE_BAR)
                            .init()
                    } else {
                        fitsSystemWindows(anno.fitsSystemWindows)
                            .statusBarDarkFont(anno.statusBarDarkFont, 0.2f)
                            .statusBarColor(anno.statusBarColor)
                            .navigationBarColor(anno.navigationBarColor).also {
                                if (anno.hideNavigationBar) {
                                    it.hideBar(BarHide.FLAG_HIDE_NAVIGATION_BAR)
                                }
                            }
                            .keyboardEnable(anno.keyboardEnable)
                            .keyboardMode(anno.keyboardMode)
                            .setOnKeyboardListener { isPopup, keyboardHeight ->
                                onKeyboardChange(isPopup, keyboardHeight)
                            }
                            .init()
                    }
                }
                return@forEach
            }
        }
    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        if (owner is ComponentActivity)
            owner.immersionBar().destroy()
        if (owner is Fragment)
            owner.immersionBar().destroy()
    }

    private fun ComponentActivity.immersionBar() = ImmersionBar.with(this)
    private fun Fragment.immersionBar() = ImmersionBar.with(this)
}

/**
 * ImmersionBar初始化，生命周期绑定
 * @receiver LifecycleOwner
 * @param onKeyboardChange Function2<[@kotlin.ParameterName] Boolean, [@kotlin.ParameterName] Int, Unit>
 */
fun LifecycleOwner.initImmersionBar(
    onKeyboardChange: (
        isPopup: Boolean,
        keyboardHeight: Int
    ) -> Unit = { _, _ -> }
) = lifecycle.addObserver(ImmersionBarObserver(onKeyboardChange))
