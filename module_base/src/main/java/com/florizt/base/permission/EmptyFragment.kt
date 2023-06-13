package com.florizt.base.permission

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.florizt.base.delegate.argument

/**
 * 权限请求透明Fragment
 * @property permissions Array<String> 权限
 * @property granted Function0<Unit> 请求通过回调
 * @property rationale Function1<MutableList<String>, Unit> 请求拒绝且不再询问回调
 * @property denied Function1<MutableList<String>, Unit> 请求拒绝回调
 * @property launcher ActivityResultLauncher<Array<String>>?
 */
class EmptyFragment : Fragment() {
    private var permissions: Array<String> by argument()

    private var granted: () -> Unit by argument()
    private var rationale: (MutableList<String>) -> Unit by argument()
    private var denied: (MutableList<String>) -> Unit by argument()

    private var launcher: ActivityResultLauncher<Array<String>>? = null

    companion object {
        fun instance(
            permissions: Array<String>,
            granted: () -> Unit,
            rationale: (MutableList<String>) -> Unit,
            denied: (MutableList<String>) -> Unit,
        ): EmptyFragment {
            return EmptyFragment().apply {
                this.permissions = permissions
                this.granted = granted
                this.rationale = rationale
                this.denied = denied
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        launcher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            val grantedList: MutableList<String> = mutableListOf()
            val rationaleList: MutableList<String> = mutableListOf()
            val deniedList: MutableList<String> = mutableListOf()
            it.forEach {
                if (it.value) {
                    grantedList.add(it.key)
                } else if (!ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), it.key)) {
                    rationaleList.add(it.key)
                } else {
                    deniedList.add(it.key)
                }
            }
            if (grantedList.size == permissions.size) {
                granted.invoke()
            } else if (rationaleList.size > 0) {
                rationale.invoke(rationaleList)
            } else {
                denied.invoke(deniedList)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        launcher?.launch(permissions)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onDestroy() {
        super.onDestroy()
        launcher?.unregister()
    }
}