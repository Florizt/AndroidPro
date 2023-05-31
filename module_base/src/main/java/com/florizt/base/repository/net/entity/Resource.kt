package com.florizt.base.repository.net.entity

/**
 * 携带有UI状态的返回值
 */
data class Resource<out T>(
    //状态
    val status: Status,
    //数据
    val data: T? = null,
    //错误信息
    val error: ResultException? = null
) {
    companion object {
        /**
         * 网络请求成功，有真实数数据
         * @param data T
         * @return Resource<T>
         */
        fun <T> success(data: T): Resource<T> {
            return Resource(status = Status.SUCCESS, data = data)
        }

        /**
         * 网络请求成功，数据为空
         * @return Resource<Nothing>
         */
        fun none(): Resource<Nothing> {
            return Resource(status = Status.NONE, data = null)
        }

        /**
         * 网络请求失败
         * @param code String
         * @param msg String
         * @return Resource<T>
         */
        fun <T> error(code: String, msg: String): Resource<T> {
            return Resource(status = Status.ERROR, error = ResultException(code, msg))
        }

        /**
         * 正在进行网络请求
         * @return Resource<T>
         */
        fun <T> loading(): Resource<T> {
            return Resource(status = Status.LOADING)
        }
    }
}

/**
 * 网络请求状态枚举
 */
enum class Status {
    SUCCESS,
    NONE,
    ERROR,
    LOADING,
}
