package com.florizt.base.repository.net.entity

/**
 * 分页结构的数据
 */
data class PageData<out T>(
    /**
     * 页码
     */
    val page: Int,
    /**
     * 每页显示记录数
     */
    val pageSize: Int,
    /**
     * 总记录数
     */
    val totalCount: Long,
    /**
     * 总页数
     */
    val totalPage: Int,
    /**
     * 分页数据
     */
    val list: List<T>
) {
    /**
     * 是否有下一页
     * @return Boolean
     */
    fun hasNextPage(): Boolean = page < totalPage
}