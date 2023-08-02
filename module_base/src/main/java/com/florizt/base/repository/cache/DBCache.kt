@file:JvmName("DBCache")

package com.florizt.base.repository.cache

import androidx.room.*
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import kotlinx.coroutines.flow.Flow
import java.lang.StringBuilder
import java.lang.reflect.ParameterizedType

/**
 * 数据库缓存
 * @param T
 * @property tableName String
 */
abstract class DBCache<T> {
    /**
     * 获取表名，默认为类名
     */
    val tableName: String
        get() {
            val clazz = (javaClass.superclass.genericSuperclass as ParameterizedType)
                .actualTypeArguments[0] as Class<*>
            val tableName = clazz.simpleName
            return tableName
        }

    /**
     * 添加单个对象
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(obj: T)

    /**
     * 删除对象
     */
    @Delete
    abstract fun delete(obj: T)

    /**
     * 条件删除
     * @param params List<Pair<Any, Any>>
     */
    fun deleteByParams(params: List<Pair<Any, Any>>) {
        val builder = StringBuilder()
        params.forEachIndexed { index, pair ->
            builder.append("${pair.first}='${pair.second}'")
            if (index != params.size - 1) {
                builder.append(" and ")
            }
        }
        deleteByParams(SimpleSQLiteQuery("delete from $tableName where ${builder.toString()}"))
    }

    /**
     * 删除全部
     * @return Int
     */
    fun deleteAll() {
        deleteAll(SimpleSQLiteQuery("delete from $tableName"))
    }

    @RawQuery
    protected abstract fun deleteByParams(query: SupportSQLiteQuery)

    @RawQuery
    protected abstract fun deleteAll(query: SupportSQLiteQuery)

    /**
     * 修改对象
     */
    @Update(onConflict = OnConflictStrategy.REPLACE)
    abstract fun update(obj: T)

    /**
     * 通过自增id查询
     * @param id Long
     * @return Flow<T?>
     */
    fun queryById(id: Long): Flow<T?> {
        return queryById(SimpleSQLiteQuery("select * from $tableName where id = ?", arrayOf<Any>(id)))
    }

    /**
     * 多参数查询
     * @param params Array<String>
     * @param value Array<Any>
     * @return Flow<List<T>?>
     */
    fun queryByParams(params: List<Pair<Any, Any>>): Flow<List<T>?> {
        val builder = StringBuilder()
        params.forEachIndexed { index, pair ->
            builder.append("${pair.first}='${pair.second}'")
            if (index != params.size - 1) {
                builder.append(" and ")
            }
        }
        return queryByParams(SimpleSQLiteQuery("select * from $tableName where ${builder.toString()}"))
    }

    /**
     * 查询所有
     * @return Flow<List<T>?>
     */
    fun queryAll(): Flow<List<T>?> {
        return queryAll(SimpleSQLiteQuery("select * from $tableName"))
    }

    /**
     * 通过自定义sql查询
     * @param sql String 条件
     */
    fun queryBySql(sql: String): Flow<List<T>?> {
        return queryBySql(SimpleSQLiteQuery("select * from $tableName $sql"))
    }

    @RawQuery
    protected abstract fun queryAll(query: SupportSQLiteQuery): Flow<List<T>?>

    @RawQuery
    protected abstract fun queryById(query: SupportSQLiteQuery): Flow<T?>

    @RawQuery
    protected abstract fun queryByParams(query: SupportSQLiteQuery): Flow<List<T>?>

    @RawQuery
    protected abstract fun queryBySql(query: SupportSQLiteQuery): Flow<List<T>?>
}