package com.florizt.base.repository.net

import com.florizt.base.repository.net.entity.ApiResponse
import com.florizt.base.repository.net.entity.ResultException
import com.florizt.base.repository.net.entity.ResultException.Companion.RESPONSEBODY_NONE_ERROR
import com.google.gson.Gson
import okhttp3.Request
import okio.Timeout
import retrofit2.*
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * 自定义retrofit CallAdapter
 * @param T
 * @property delegate Call<T>
 * @property gson Gson
 * @property successCode String 请求正常code
 * @property mapper Function1<[@kotlin.ParameterName] Any, ApiResponse<*>> 请求体映射
 * @constructor
 */
class ApiResponseCall<T>(
    private val delegate: Call<T>,
    private val gson: Gson,
    private val successCode: String,
    private val mapper: ((T) -> ApiResponse<*>),
) : Call<ApiResponse<*>> {

    override fun clone(): Call<ApiResponse<*>> {
        return ApiResponseCall(delegate.clone(), gson, successCode, mapper)
    }

    override fun execute(): Response<ApiResponse<*>> {
        return response(delegate.execute())
    }

    override fun enqueue(callback: Callback<ApiResponse<*>>) {
        delegate.enqueue(object : Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {
                callback.onResponse(this@ApiResponseCall, response(response))
            }

            override fun onFailure(call: Call<T>, t: Throwable) {
                callback.onResponse(this@ApiResponseCall, ResultException.handException(t).run {
                    Response.success(ApiResponse(code, msg, null))
                })
            }
        })
    }

    private fun response(response: Response<T>): Response<ApiResponse<*>> = try {
        if (response.isSuccessful) {
            val body = response.body()
            if (body != null) {
                val t = mapper.invoke(body)
                if (t.code == successCode) {
                    t.isSuccess = true
                    Response.success(t)
                } else {
                    Response.success(ApiResponse(t.code, t.msg, null))
                }
            } else {
                Response.success(ApiResponse(RESPONSEBODY_NONE_ERROR.first, RESPONSEBODY_NONE_ERROR.second, null))
            }
        } else {
            val error = response.errorBody()?.string()
            if (error != null) {
                try {
                    val data = gson.fromJson(error, ApiResponse::class.java)
                    Response.success(ApiResponse(data.code, data.msg, null))
                } catch (e: Exception) {
                    ResultException.handException(e).run {
                        Response.success(ApiResponse(code, msg, null))
                    }
                }
            } else {
                Response.success(ApiResponse(RESPONSEBODY_NONE_ERROR.first, RESPONSEBODY_NONE_ERROR.second, null))
            }
        }
    } catch (e: Exception) {
        ResultException.handException(e).run {
            Response.success(ApiResponse(code, msg, null))
        }
    }

    override fun isExecuted(): Boolean = delegate.isExecuted

    override fun cancel() {
        delegate.cancel()
    }

    override fun isCanceled(): Boolean = delegate.isCanceled

    override fun request(): Request = delegate.request()

    override fun timeout(): Timeout = delegate.timeout()
}

class ApiResponseCallAdapter<T>(
    private val responseType: Type,
    private val gson: Gson,
    private val successCode: String,
    private val mapper: ((T) -> ApiResponse<*>),
) : CallAdapter<T, ApiResponseCall<T>> {

    override fun responseType(): Type = responseType

    override fun adapt(call: Call<T>): ApiResponseCall<T> {
        return ApiResponseCall(call, gson, successCode, mapper)
    }
}

class ApiResponseCallAdapterFactory<T>(
    private val gson: Gson,
    private val successCode: String,
    private val mapper: ((T) -> ApiResponse<*>),
) : CallAdapter.Factory() {

    override fun get(
        returnType: Type,
        annotations: Array<Annotation>,
        retrofit: Retrofit,
    ): CallAdapter<*, *>? {

        if (Call::class.java != getRawType(returnType)) {
            return null
        }

        check(returnType is ParameterizedType) {
            "return type must be parameterized as Call<ApiResponse<<Foo>> or Call<ApiResponse<out Foo>>"
        }

        val responseType = getParameterUpperBound(0, returnType)
        if (getRawType(responseType) != ApiResponse::class.java) {
            return null
        }

        return ApiResponseCallAdapter<T>(responseType, gson, successCode, mapper)
    }
}
