package com.florizt.base.repository.net.entity

import android.net.ParseException
import android.os.Parcelable
import com.google.gson.JsonParseException
import kotlinx.parcelize.Parcelize
import org.json.JSONException
import retrofit2.HttpException
import java.net.SocketException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.net.UnknownServiceException
import javax.net.ssl.SSLHandshakeException

/**
 * 当不是请求成功错误码时，抛出的自定义异常
 * @property code String
 * @property msg String
 * @constructor
 */
@Parcelize
data class ResultException(val code: String, val msg: String) : Throwable(msg), Parcelable {
    companion object {
        //对应HTTP的状态码
        @JvmField
        val UNAUTHORIZED = Pair("401", "网络错误")

        @JvmField
        val FORBIDDEN = Pair("403", "网络错误")

        @JvmField
        val NOT_FOUND = Pair("404", "网络错误")

        @JvmField
        val REQUEST_TIMEOUT = Pair("408", "网络连接超时")

        @JvmField
        val GATEWAY_TIMEOUT = Pair("504", "网络连接超时")

        @JvmField
        val INTERNAL_SERVER_ERROR = Pair("500", "服务器错误")

        @JvmField
        val BAD_GATEWAY = Pair("502", "服务器错误")

        @JvmField
        val SERVICE_UNAVAILABLE = Pair("503", "服务器错误")

        // 自定义HTTP错误码
        @JvmField
        val UNKNOWN = Pair("1000", "未知错误")

        @JvmField
        val PARSE_ERROR = Pair("1001", "解析错误")

        @JvmField
        val UNKNOW_HOST = Pair("1002", "网络错误，请切换网络重试")

        @JvmField
        val SSL_ERROR = Pair("1003", "证书验证失败")

        @JvmField
        val FORMAT_ERROR = Pair("1004", "数字格式化异常")

        @JvmField
        val RESPONSEBODY_NONE_ERROR = Pair("1005", "响应体为空")

        @JvmStatic
        fun handException(t: Throwable): ResultException {
            return when (t) {
                is HttpException -> {
                    val c = t.code().toString()
                    var s = "网络错误"
                    if (c == UNAUTHORIZED.first.apply { s = UNAUTHORIZED.second }
                        || c == FORBIDDEN.first.apply { s = FORBIDDEN.second }
                        || c == NOT_FOUND.first.apply { s = NOT_FOUND.second }
                        || c == REQUEST_TIMEOUT.first.apply { s = REQUEST_TIMEOUT.second }
                        || c == GATEWAY_TIMEOUT.first.apply { s = GATEWAY_TIMEOUT.second }
                        || c == INTERNAL_SERVER_ERROR.first.apply { s = INTERNAL_SERVER_ERROR.second }
                        || c == BAD_GATEWAY.first.apply { s = BAD_GATEWAY.second }
                        || c == SERVICE_UNAVAILABLE.first.apply { s = SERVICE_UNAVAILABLE.second }) {
                        ResultException(c, s)
                    } else {
                        ResultException(c, s)
                    }
                }

                is SocketException,
                is SocketTimeoutException,
                -> {
                    ResultException(REQUEST_TIMEOUT.first, REQUEST_TIMEOUT.second)
                }

                is JsonParseException,
                is JSONException,
                is ParseException,
                -> {
                    ResultException(PARSE_ERROR.first, PARSE_ERROR.second)
                }

                is SSLHandshakeException -> {
                    ResultException(SSL_ERROR.first, SSL_ERROR.second)
                }

                is UnknownHostException,
                is UnknownServiceException,
                -> {
                    ResultException(UNKNOW_HOST.first, UNKNOW_HOST.second)
                }

                is NumberFormatException -> {
                    ResultException(FORMAT_ERROR.first, FORMAT_ERROR.second)
                }

                is ResultException -> {
                    t
                }

                else -> {
                    ResultException(UNKNOWN.first, UNKNOWN.second)
                }
            }
        }
    }
}