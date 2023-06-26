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
import com.florizt.base.delegate.noOpDelegate
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlin.coroutines.CoroutineContext

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
 * 点击防抖
 * @receiver View
 * @param thresholdMillis Long 防抖间隔，默认500ms
 * @param dispatcher CoroutineDispatcher 事件执行线程，默认主线程
 * @param scope CoroutineScope 作用域
 * @param block Function0<Unit> 执行体
 * @return Job
 */
@OptIn(FlowPreview::class)
@JvmOverloads
inline fun View.clickFlow(
    thresholdMillis: Long = 500L,
    dispatcher: CoroutineDispatcher = Dispatchers.Main,
    scope: CoroutineScope,
    crossinline block: () -> Unit,
) = callbackFlow {
    setOnClickListener { trySend(Unit) }
    awaitClose { setOnClickListener(null) }
}.throttleFirst(thresholdMillis)
    .onEach { block() }
    .flowOn(dispatcher)
    .launchIn(scope)

@FlowPreview
fun <T> Flow<T>.throttleFirst(thresholdMillis: Long): Flow<T> = flow {
    var lastTime = 0L
    collect {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastTime > thresholdMillis) {
            lastTime = currentTime
            emit(it)
        }
    }
}

/**
 * 输入框限流（搜索联想场景适用）
 * @receiver EditText
 * @param timeoutMillis Long 指定时间内的值只接收最新的一个，其他的过滤掉，默认400ms
 * @param scope CoroutineScope 作用域
 * @param textChange Function1<String, Unit> 文本改变状态，包含空状态，适用于输入框清空按钮显示逻辑
 * @param fetch Function1<String, Flow<T>> 当文本不为空该处理的逻辑，如网络请求进行搜索联想
 * @param result Function1<T, Unit> fetch执行后的结果
 * @return Job
 */
@JvmOverloads
@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
inline fun <T> EditText.textChangeFlow(
    timeoutMillis: Long = 400L,
    scope: CoroutineScope,
    crossinline textChange: (String) -> Unit = {},
    crossinline fetch: (String) -> Flow<T>,
    crossinline result: (T) -> Unit,
) = callbackFlow<String> {
    val watcher = object : TextWatcher by noOpDelegate() {
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            s?.let { trySend(it.toString()) }
        }
    }
    addTextChangedListener(watcher)
    awaitClose { removeTextChangedListener(watcher) }
}.debounce(timeoutMillis)
    .onEach { textChange(it) }
    .flowOn(Dispatchers.Main)
    .filter { it.isNotEmpty() }
    .flatMapLatest { fetch(it) }
    .flowOn(Dispatchers.IO)
    .onEach { result(it) }
    .launchIn(scope)





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
//                nodataView.click(click = nodata_click)
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
//                errorView.click(click = errorClick)
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