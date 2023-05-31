package com.florizt.base.ext

import android.util.TypedValue
import com.florizt.base.app.ContextWrapper

/**
 * 单位转换，px2dp(Int->Int)
 */
val Int.dp
    get() = dp().toInt()

/**
 * 单位转换，px2dp(Int->Float)
 */
val Int.dpf
    get() = dp()

/**
 * 单位转换，px2dp(Float->Int)
 */
val Float.dp: Int
    get() = dp().toInt()

/**
 * 单位转换，px2dp(Float->Float)
 */
val Float.dpf: Float
    get() = dp()

private fun Int.dp(): Float {
    return this.toFloat().dp()
}

private fun Float.dp(): Float {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this,
        ContextWrapper.context.resources.displayMetrics
    )
}

/**
 * 单位转换，dp2px(Int->Int)
 */
val Int.px
    get() = px().toInt()

/**
 * 单位转换，dp2px(Int->Float)
 */
val Int.pxf
    get() = px()

/**
 * 单位转换，dp2px(Float->Int)
 */
val Float.px
    get() = px().toInt()

/**
 * 单位转换，dp2px(Float->Float)
 */
val Float.pxf
    get() = px()


private fun Int.px(): Float {
    return this.toFloat().px()
}

private fun Float.px(): Float {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_PX,
        this,
        ContextWrapper.context.resources.displayMetrics
    )
}

/**
 * 单位转换，px2sp(Int->Int)
 */
val Int.sp
    get() = sp().toInt()

/**
 * 单位转换，px2sp(Int->Float)
 */
val Int.spf
    get() = sp()

/**
 * 单位转换，px2sp(Float->Int)
 */
val Float.sp
    get() = sp().toInt()

/**
 * 单位转换，px2sp(Float->Float)
 */
val Float.spf
    get() = sp()

private fun Int.sp(): Float {
    return this.toFloat().sp()
}

private fun Float.sp(): Float {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_SP,
        this,
        ContextWrapper.context.resources.displayMetrics
    )
}
