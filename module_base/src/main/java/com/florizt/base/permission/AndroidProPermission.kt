@file:JvmName("AndroidProPermission")

package com.florizt.base.permission

import androidx.fragment.app.FragmentActivity
import com.florizt.base.app.AppLogger
import com.florizt.base.ui.AppStackManager

object AndroidProPermission {

    /**
     * 权限申请
     * @param permissions Array<String> 权限
     * @param granted Function0<Unit> 请求通过回调
     * @param rationale Function1<MutableList<String>, Unit> 请求拒绝且不再询问回调
     * @param denied Function1<MutableList<String>, Unit> 请求拒绝回调
     */
    @JvmStatic
    @JvmOverloads
    fun request(
        permissions: Array<String>,
        granted: () -> Unit = {},
        rationale: (MutableList<String>) -> Unit = {},
        denied: (MutableList<String>) -> Unit = {},
    ) {
        AppStackManager.currentActivity()?.apply {
            if (this is FragmentActivity)
                supportFragmentManager.apply {
                    if (findFragmentByTag("permission") == null) {
                        beginTransaction().add(
                            EmptyFragment.instance(permissions, granted, rationale, denied),
                            "permission"
                        )
                            .commitAllowingStateLoss()
                    }
                }
            else AppLogger.log(AppLogger.LEVEL.I, "please extend FragmentActivity")
        }
    }
}