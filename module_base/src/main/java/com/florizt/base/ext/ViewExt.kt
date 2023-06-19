package com.florizt.base.ext

import android.os.Build
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.text.method.LinkMovementMethod
import android.view.*
import android.view.ViewGroup.MarginLayoutParams
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.SwitchCompat
import androidx.core.view.forEach
import androidx.core.view.marginTop
import androidx.recyclerview.widget.*

/**
 * 点击事件
 * @receiver View
 * @param click Function0<Any?>
 * @param throttleFirst Boolean 是否设置防抖动，默认true
 */
@JvmOverloads
inline fun View.click(throttleFirst: Boolean = true, crossinline click: () -> Unit) {
    val minTime = 500L
    var lastTime = 0L
    setOnClickListener {
        val tmpTime = System.currentTimeMillis()
        if (throttleFirst) {
            if (tmpTime - lastTime > minTime) {
                click()
                lastTime = tmpTime
            }
        } else {
            click()
        }
    }
}

/**
 * 长按事件
 * @receiver View
 * @param longClick Function0<Any?>
 */
inline fun View.longClick(crossinline longClick: () -> Unit) {
    setOnLongClickListener {
        longClick()
        true
    }
}

/**
 * EditText文本监听
 * @receiver EditText
 * @param textChanged Function4<CharSequence, Int, Int, Int, Any?>
 */
@JvmOverloads
fun EditText.textChanged(
    beforeTextChanged: ((CharSequence, Int, Int, Int) -> Unit)? = null,
    textChanged: ((CharSequence, Int, Int, Int) -> Unit)? = null,
    afterTextChanged: ((Editable) -> Unit)? = null,
) {
    addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
            beforeTextChanged?.invoke(charSequence, i, i1, i2)
        }

        override fun onTextChanged(text: CharSequence, i: Int, i1: Int, i2: Int) {
            textChanged?.invoke(text, i, i1, i2)
        }

        override fun afterTextChanged(editable: Editable) {
            afterTextChanged?.invoke(editable)
        }
    })
}

/**
 * CheckBox的CheckedChanged事件监听
 * @receiver CheckBox
 * @param checkedChanged Function2<CompoundButton, Boolean, Any?>
 */
inline fun CheckBox.checkedChanged(crossinline checkedChanged: (CompoundButton, Boolean) -> Unit) {
    setOnCheckedChangeListener { compoundButton, b -> checkedChanged(compoundButton, b) }
}

/**
 * Switch的CheckedChanged事件监听
 * @receiver Switch
 * @param checkedChanged Function2<CompoundButton, Boolean, Any?>
 */
inline fun SwitchCompat.checkedChanged(crossinline checkedChanged: (CompoundButton, Boolean) -> Unit) {
    setOnCheckedChangeListener { buttonView, isChecked ->
        checkedChanged(buttonView, isChecked)
    }
}

/**
 * ScrollView滚动监听
 * @receiver ScrollView
 * @param scrollChanged Function5<View, Int, Int, Int, Int, Any?>
 */
@RequiresApi(Build.VERSION_CODES.M)
inline fun ScrollView.scrollChanged(crossinline scrollChanged: (View, Int, Int, Int, Int) -> Unit) {
    setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
        scrollChanged(v, scrollX, scrollY, oldScrollX, oldScrollY)
    }
}

/**
 * 获取焦点
 */
fun View.focus() {
    isFocusableInTouchMode = true
    requestFocus()
}

/**
 * 清除焦点
 */
fun View.unfocus() {
    clearFocus()
}

/**
 * view的焦点发生变化的事件绑定
 */
inline fun View.focusChanged(crossinline focusChanged: (View, Boolean) -> Unit) {
    setOnFocusChangeListener { view, b -> focusChanged(view, b) }
}

fun View.visible() {
    visibility = View.VISIBLE
}

fun View.gone() {
    visibility = View.GONE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

infix fun View.width(w: Int) {
    layoutParams = layoutParams.apply {
        width = w
    }
}

infix fun View.height(h: Int) {
    layoutParams = layoutParams.apply {
        height = h
    }
}

fun View.widthHeight(w: Int, h: Int) {
    layoutParams = layoutParams.apply {
        width = w
        height = h
    }
}

infix fun View.marginTop(margin: Int) {
    if (layoutParams is MarginLayoutParams) {
        layoutParams = (layoutParams as MarginLayoutParams).apply {
            topMargin = margin
        }
    }
}

infix fun View.marginBottom(margin: Int) {
    if (layoutParams is MarginLayoutParams) {
        layoutParams = (layoutParams as MarginLayoutParams).apply {
            bottomMargin = margin
        }
    }
}

infix fun View.marginStart(margin: Int) {
    if (layoutParams is MarginLayoutParams) {
        layoutParams = (layoutParams as MarginLayoutParams).apply {
            leftMargin = margin
        }
    }
}

infix fun View.marginEnd(margin: Int) {
    if (layoutParams is MarginLayoutParams) {
        layoutParams = (layoutParams as MarginLayoutParams).apply {
            rightMargin = margin
        }
    }
}

infix fun View.paddingTop(padding: Int) {
    setPadding(paddingLeft, padding, paddingRight, paddingBottom)
}

infix fun View.paddingBottom(padding: Int) {
    setPadding(paddingLeft, paddingTop, paddingRight, padding)
}

infix fun View.paddingStart(padding: Int) {
    setPadding(padding, paddingTop, paddingRight, paddingBottom)
}

infix fun View.paddingEnd(padding: Int) {
    setPadding(paddingLeft, paddingTop, padding, paddingBottom)
}

fun View.selected() {
    isSelected = true
}

fun View.unselected() {
    isSelected = false
}

fun View.enable() {
    isEnabled = true
}

fun View.disable() {
    isEnabled = false
}

fun TextView.movementMethod() {
    setMovementMethod(LinkMovementMethod.getInstance())
}

// 0-IDLE,1-LOADING, 2-NODATA,3-ERROR,4-SUCCESS
const val IDLE = 0
const val LOADING = 1
const val NODATA = 2
const val ERROR = 3
const val SUCCESS = 4

/**
 * 状态view
 * @receiver ViewGroup
 * @param status Int
 * @param view_loading Int?
 * @param view_nodata Int?
 * @param view_error Int?
 * @param errorClick Function0<Any?>
 */
@JvmOverloads
fun ViewGroup.statusView(
    status: Int = IDLE,
    view_loading: Int?,
    view_nodata: Int?,
    nodata_click: () -> Unit,
    view_error: Int?,
    errorClick: () -> Unit,
) {
    if (childCount == 1) {
        getChildAt(0).tag = "view_success"
    }
    val layoutInflater = LayoutInflater.from(context)
    when (status) {
        IDLE,
        SUCCESS,
        -> {
            forEach {
                if (TextUtils.equals(it.tag as String, "view_loading")
                    || TextUtils.equals(it.tag as String, "view_nodata")
                    || TextUtils.equals(it.tag as String, "view_error")
                ) {
                    removeView(it)
                } else if (TextUtils.equals(it.tag as String, "view_success")) {
                    it.visibility = View.VISIBLE
                }
            }
        }

        LOADING -> {
            forEach {
                if (TextUtils.equals(it.tag as String, "view_nodata")
                    || TextUtils.equals(it.tag as String, "view_error")
                ) {
                    removeView(it)
                } else if (TextUtils.equals(it.tag as String, "view_success")) {
                    it.visibility = View.GONE
                }
            }
            view_loading?.let {
                val loadingView = layoutInflater.inflate(it, null, false)
                loadingView.tag = "view_loading"
                addView(
                    loadingView,
                    ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                )
            }
        }

        NODATA -> {
            forEach {
                if (TextUtils.equals(it.tag as String, "view_loading")
                    || TextUtils.equals(it.tag as String, "view_error")
                ) {
                    removeView(it)
                } else if (TextUtils.equals(it.tag as String, "view_success")) {
                    it.visibility = View.GONE
                }
            }
            view_nodata?.let {
                val nodataView = layoutInflater.inflate(it, null, false)
                nodataView.tag = "view_nodata"
                addView(
                    nodataView,
                    ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                )
                nodataView.click(click = nodata_click)
            }
        }

        ERROR -> {
            forEach {
                if (TextUtils.equals(it.tag as String, "view_loading")
                    || TextUtils.equals(it.tag as String, "view_nodata")
                ) {
                    removeView(it)
                } else if (TextUtils.equals(it.tag as String, "view_success")) {
                    it.visibility = View.GONE
                }
            }
            view_error?.let {
                val errorView = layoutInflater.inflate(it, null, false)
                errorView.tag = "view_error"
                addView(
                    errorView,
                    ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                )
                errorView.click(click = errorClick)
            }
        }

        else -> {
            forEach {
                if (TextUtils.equals(it.tag as String, "view_loading")
                    || TextUtils.equals(it.tag as String, "view_nodata")
                    || TextUtils.equals(it.tag as String, "view_error")
                ) {
                    removeView(it)
                } else if (TextUtils.equals(it.tag as String, "view_success")) {
                    it.visibility = View.VISIBLE
                }
            }
        }
    }
}