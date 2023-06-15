package com.florizt.base.ext

import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/**
 * 安全执行
 * @param block Function0<Unit>
 */
@JvmName(name = "safe0")
inline fun safe(block: () -> Unit) {
    try {
        block()
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

/**
 * 安全执行
 * @param block Function0<Unit>
 */
@JvmName(name = "safe1")
@JvmOverloads
inline fun safe(block: () -> Unit, finally: () -> Unit = {}) {
    try {
        block()
    } catch (e: Exception) {
        e.printStackTrace()
    } finally {
        finally()
    }
}

/**
 * 安全执行
 * @param block Function0<Unit>
 */
@JvmName(name = "safe2")
inline fun <T> safe(block: () -> T, error: (Exception) -> T): T {
    try {
        return block()
    } catch (e: Exception) {
        e.printStackTrace()
        return error(e)
    }
}

/**
 * 安全执行
 * @param block Function0<Unit>
 */
@JvmName(name = "safe3")
@JvmOverloads
inline fun <T> safe(block: () -> T, error: (Exception) -> T, finally: () -> Unit = {}): T {
    try {
        return block()
    } catch (e: Exception) {
        e.printStackTrace()
        return error(e)
    } finally {
        finally()
    }
}

/**
 * 执行检查，执行完unpass后，通过[checkout]函数通知该方法继续执行
 * @param predicate Function0<Boolean> 条件断言
 * @param unpass Function0<Unit> 不通过
 * @param pass Function0<Unit> 通过
 */
suspend fun check(
    predicate: () -> Boolean,
    unpass: () -> Unit,
    pass: () -> Unit,
) {
    CheckReq.job = null
    suspendCancellableCoroutine {
        if (predicate()) {
            pass()
            it.resume(0)
        } else {
            unpass()
            CheckReq.job = it
        }
    }
    CheckReq.job?.apply {
        check(predicate, unpass, pass)
    }
}

/**
 * 通知[check]继续执行
 * @param block Function0<Unit>
 */
fun checkout(block: () -> Unit) {
    block()
    CheckReq.job?.resume(0)
}

object CheckReq {
    var job: CancellableContinuation<Int>? = null
}