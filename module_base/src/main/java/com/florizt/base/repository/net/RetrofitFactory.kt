package com.florizt.base.repository.net

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
    private var _baseUrl: String? = null

    /**
     * 网络请求域名
     */
    val baseUrl: String = _baseUrl ?: throw IllegalArgumentException("retrofit init failed")
    fun setBaseUrl(baseUrl: String): RetrofitFactory {
        _baseUrl = baseUrl
        return this
    }

    private var _successCode: String? = null

    /**
     * 网络请求正常code
     */
    val successCode: String = _successCode ?: throw IllegalArgumentException("retrofit init failed")
    fun setSuccessCode(successCode: String): RetrofitFactory {
        _successCode = successCode
        return this
    }

    private var _mapper: ((any: Any) -> ApiResponse<*>)? = null

    /**
     * 网络请求响应体和封装的ApiResponse映射函数
     */
    val mapper: (any: Any) -> ApiResponse<*> = _mapper ?: throw IllegalArgumentException("retrofit init failed")
    fun setMapper(mapper: (any: Any) -> ApiResponse<*>): RetrofitFactory {
        _mapper = mapper
        return this
    }


    private var _interceptors: List<Interceptor>? = null

    /**
     * 网络请求拦截器
     */
    val interceptors: List<Interceptor> = _interceptors ?: arrayListOf()
    fun setInterceptors(interceptors: List<Interceptor>): RetrofitFactory {
        _interceptors = interceptors
        return this
    }

    /**
     * okhttp客户端
     */
    private val okHttpClient by lazy {
        OkHttpClient.Builder()
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .connectTimeout(15, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .apply {
                interceptors.apply {
                    forEach {
                        addInterceptor(it)
                    }
                }
            }
            .build()
    }

    /**
     * retrofit对象
     */
    private val retrofit by lazy {
        if (_baseUrl.isNullOrEmpty() || _successCode.isNullOrEmpty() || _mapper == null)
            throw IllegalArgumentException("retrofit init failed")
        val gson = Gson()
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addCallAdapterFactory(ApiResponseCallAdapterFactory(gson, successCode, mapper))
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    /**
     * 获取ApiService的动态代理对象
     * @param service Class<S>
     * @return S
     */
    fun <S> getService(service: Class<S>): S = retrofit.create(service)
}