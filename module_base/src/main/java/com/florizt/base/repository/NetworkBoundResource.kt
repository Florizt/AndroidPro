package com.florizt.base.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.florizt.base.repository.net.entity.ApiResponse
import com.florizt.base.repository.net.entity.Resource
import com.florizt.base.repository.net.entity.PageData
import com.florizt.base.repository.net.entity.ResultException
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

fun <Remote, Local : Any> paging(
    pageSize: Int,
    prefetchDistance: Int,
    fetch: suspend (Int, Int) -> ApiResponse<PageData<Remote>>,
    transfer: suspend (PageData<Remote>) -> PageData<Local>,
): Flow<PagingData<Local>> {
    return Pager(
        config = PagingConfig(
            pageSize,
            initialLoadSize = pageSize,
            prefetchDistance = prefetchDistance,
            enablePlaceholders = false
        ),
        pagingSourceFactory = {
            object : PagingSource<Int, Local>() {
                override fun getRefreshKey(state: PagingState<Int, Local>): Int? = null

                override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Local> {
                    val position = params.key ?: 1
                    return fetch.invoke(position, params.loadSize).run {
                        if (isSuccess == true) {
                            data?.run {
                                transfer(this).run {
                                    val nextKey = if (this.hasNextPage()) position + 1 else null
                                    LoadResult.Page(
                                        data = this.list,
                                        prevKey = null,
                                        nextKey = nextKey
                                    )
                                }
                            } ?: run {
                                LoadResult.Error(
                                    ResultException(
                                        ResultException.RESPONSEBODY_NONE_ERROR.first,
                                        ResultException.RESPONSEBODY_NONE_ERROR.second
                                    )
                                )
                            }
                        } else {
                            LoadResult.Error(ResultException(this.code, this.msg))
                        }
                    }
                }
            }
        }
    ).flow
}