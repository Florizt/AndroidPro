package com.florizt.base.repository

import com.florizt.base.repository.net.entity.ApiResponse
import com.florizt.base.repository.net.entity.Resource
import com.florizt.base.repository.net.RetrofitFactory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

/**
 * 缓存 + 网络请求
 * @param cache Function0<Flow<Local?>> 缓存请求
 * @param fetch SuspendFunction0<ApiResponse<Remote>> 网络请求
 * @param transfer Function1<Remote, Local> 网络请求实体映射成本地实体
 * @param fetch2Cache SuspendFunction1<Local, Unit> 网络请求缓存至本地
 * @return Flow<Resource<Local>>
 */
inline fun <Local, Remote> networkBoundResource(
    crossinline cache: () -> Flow<Local?>,
    crossinline fetch: suspend () -> ApiResponse<Remote>,
    crossinline transfer: (Remote) -> Local,
    crossinline fetch2Cache: suspend (Local) -> Unit = {},
) = flow {
    emit(Resource.loading())

    emitAll(cache().map {
        it?.run {
            Resource.success(it)
        } ?: run {
            Resource.none()
        }
    })

    emit(fetch().run {
        if (isSuccess == true) {
            data?.run {
                Resource.success(transfer(this).apply { fetch2Cache(this) })
            } ?: run {
                Resource.none()
            }
        } else {
            Resource.error(code, msg)
        }
    })
}

/**
 * 仅网络请求
 * @param fetch SuspendFunction0<ApiResponse<Remote>> 网络请求
 * @param transfer Function1<Remote, Local> 网络请求实体映射成本地实体
 * @param fetch2Cache SuspendFunction1<Local, Unit> 网络请求缓存至本地
 * @return Flow<Resource<Local>>
 */
inline fun <Local, Remote> networkBoundResourceNoCache(
    crossinline fetch: suspend () -> ApiResponse<Remote>,
    crossinline transfer: (Remote) -> Local,
    crossinline fetch2Cache: suspend (Local) -> Unit = {},
) = flow {
    emit(Resource.loading())

    emit(fetch().run {
        if (isSuccess == true) {
            data?.run {
                Resource.success(transfer(this).apply { fetch2Cache(this) })
            } ?: run {
                Resource.none()
            }
        } else {
            Resource.error(code, msg)
        }
    })
}

/**
 * 仅缓存请求
 * @param cache Function0<Flow<Local?>> 缓存请求
 * @return Flow<Resource<Local & Any>>
 */
inline fun <Local> networkBoundResourceOnlyCache(
    crossinline cache: () -> Flow<Local?>,
) = flow {
    emit(Resource.loading())

    emitAll(cache().map {
        it?.run {
            Resource.success(it)
        } ?: run {
            Resource.none()
        }
    })
}