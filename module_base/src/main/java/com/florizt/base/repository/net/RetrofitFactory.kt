package com.florizt.base.repository.net

import androidx.room.ext.T
import com.florizt.base.repository.net.RetrofitFactory.retrofit
import com.florizt.base.repository.net.entity.ApiResponse
import com.google.gson.Gson
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit

/**
 * 工厂方式获取[Retrofit]
 */
object RetrofitFactory {
    /**
     * okhttp客户端构造器
     */
    private val okHttpClientBuilder by lazy {
        OkHttpClient.Builder()
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .connectTimeout(15, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
    }

    private var _retrofit: Retrofit? = null

    /**
     * retrofit对象
     */
    val retrofit: Retrofit
        get() = _retrofit ?: throw IllegalArgumentException("retrofit init failed, please load initRetrofit()")

    /**
     * retrofit初始化
     * @param baseUrl String 网络请求域名
     * @param successCode String 网络请求正常code
     * @param mapper Function1<T, ApiResponse<*>> 网络请求响应体和封装的ApiResponse映射函数
     * @param interceptors List<Interceptor> 网络请求拦截器
     */
    fun <T> initRetrofit(
        baseUrl: String,
        successCode: String,
        mapper: (T) -> ApiResponse<*>,
        interceptors: List<Interceptor>,
    ) {
        val okHttpClient = okHttpClientBuilder.apply {
            interceptors.apply {
                forEach {
                    addInterceptor(it)
                }
            }
        }.build()

        val gson = Gson()
        _retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addCallAdapterFactory(
                ApiResponseCallAdapterFactory(
                    gson,
                    successCode,
                    mapper
                )
            )
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

    }
}

/**
 * 获取ApiService的动态代理对象
 * @return S
 */
inline fun <reified S> service(): S = retrofit.create(S::class.java)