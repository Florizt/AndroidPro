package com.florizt.base.repository.net.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

/**
 * 网络请求响应体，需要通过[RetrofitFactory][setMapper()]设置数据映射
 * 将服务端返回的数据映射成[ApiResponse]类型
 * @param T
 * @property code String
 * @property msg String
 * @property data T?
 * @property isSuccess Boolean? 请求是否成功
 * @constructor
 */
@Parcelize
data class ApiResponse<T>(
    val code: String,
    val msg: String,
    val data: @RawValue T?,
    var isSuccess: Boolean? = null,
) : Parcelable