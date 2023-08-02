package com.florizt.base.repository.net

import com.florizt.base.app.AppLogger
import com.florizt.base.ext.safe
import okhttp3.Interceptor
import okhttp3.Response
import okio.Buffer
import org.json.JSONArray
import org.json.JSONObject
import java.lang.StringBuilder
import java.nio.charset.Charset

class LoggingInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        return chain.proceed(request).apply {
            safe {
                val headers = StringBuilder().apply {
                    request.headers.forEachIndexed { index, pair ->
                        append("${pair.first}:${pair.second}")
                        if (index != request.headers.size - 1) {
                            append(",")
                            append("\n")
                            append("        ")
                        }
                    }
                }.toString().run {
                    if (this.isEmpty()) {
                        "null"
                    } else {
                        this
                    }
                }

                val requestBody = request.body?.run {
                    Buffer().apply { writeTo(this) }.readString(Charset.defaultCharset())
                }?.run {
                    if (startsWith("{") && endsWith("}")) {
                        JSONObject(this).toString(1)
                    } else if (startsWith("[") && endsWith("]")) {
                        JSONArray(this).toString(1)
                    } else {
                        this
                    }
                } ?: "null"

                val reponseBody = body?.run {
                    source().apply { request(Long.MAX_VALUE) }.buffer.clone().readString(Charset.defaultCharset())
                }?.run {
                    if (startsWith("{") && endsWith("}")) {
                        JSONObject(this).toString(1)
                    } else if (startsWith("[") && endsWith("]")) {
                        JSONArray(this).toString(1)
                    } else {
                        this
                    }
                } ?: "null"

                AppLogger.log(
                    AppLogger.LEVEL.NORMAL,
                    """
                                                                                
发送请求:
    method:${request.method},
    url:${request.url},
    headers:
        $headers
    请求body:$requestBody
收到响应:
    code:$code
    响应body:$reponseBody
                                                                       
            """.trimIndent()
                )
            }
        }
    }
}