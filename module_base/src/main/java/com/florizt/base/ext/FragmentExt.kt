package com.florizt.base.ext

import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import java.lang.reflect.Field

/**
 * 安全show
 * @receiver DialogFragment
 * @param manager FragmentManager
 * @param tag String?
 */
fun DialogFragment.safeShow(manager: FragmentManager, tag: String?) {
    try {
        val c = Class.forName("androidx.fragment.app.DialogFragment")
        val con = c.getConstructor()
        val obj = con.newInstance()
        val dismissed: Field = c.getDeclaredField("mDismissed")
        dismissed.setAccessible(true)
        dismissed.set(obj, false)
        val shownByMe: Field = c.getDeclaredField("mShownByMe")
        shownByMe.setAccessible(true)
        shownByMe.set(obj, false)
    } catch (e: Exception) {
        e.printStackTrace()
    }
    val ft: FragmentTransaction = manager.beginTransaction()
    ft.add(this, tag)
    ft.commitAllowingStateLoss()
}

/**
 * 安全dismiss
 * @receiver DialogFragment
 */
fun DialogFragment.safeDismiss() {
    dismissAllowingStateLoss()
}

/**
 * 安全获取Activity
 * @receiver Fragment
 * @param block [@kotlin.ExtensionFunctionType] Function1<FragmentActivity, Unit>
 */
fun Fragment.safeRequireActivity(block: FragmentActivity.() -> Unit) {
    safe { requireActivity().block() }
}

