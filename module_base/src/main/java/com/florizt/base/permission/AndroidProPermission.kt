package com.florizt.base.permission

import androidx.fragment.app.FragmentActivity
import com.florizt.base.ui.AppStackManager

object AndroidProPermission {

    @JvmStatic
    @JvmOverloads
    fun requestPermission(
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
        }
    }
}