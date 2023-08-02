@file:JvmName("RecyclerViewExt")

package com.florizt.base.ext

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

/**
 * RecyclerView快速绑定，减少Adapter创建
 * @receiver RecyclerView
 * @param scope CoroutineScope 生命周期域
 * @param areItemsTheSame Function2<T, T, Boolean> 是否同一个item
 * @param areContentsTheSame Function2<T, T, Boolean> 是否同一个content
 * @param layout LayoutManager LayoutManager，可用[linear]或者[grid]
 * @param item Flow<List<T>> 数据
 * @param itemBinding ItemBinding<T> 布局
 */
inline fun <reified T : Any> RecyclerView.bind(
    scope: CoroutineScope,
    crossinline areItemsTheSame: (T, T) -> Boolean,
    crossinline areContentsTheSame: (T, T) -> Boolean,
    layout: LayoutManager,
    item: Flow<List<T>>,
    itemBinding: ItemBinding<T>,
) {
    val listAdapter = object : ListAdapter<T, VH<T>>(
        object : DiffUtil.ItemCallback<T>() {
            override fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
                return areItemsTheSame.invoke(oldItem, newItem)
            }

            override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
                return areContentsTheSame.invoke(oldItem, newItem)
            }

        }
    ) {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH<T> {
            val view = DataBindingUtil.inflate<ViewDataBinding?>(
                LayoutInflater.from(context),
                itemBinding.layoutId, null, false
            )?.root ?: View.inflate(context, itemBinding.layoutId, null)
            return VH(itemBinding.variableId, view, itemBinding.bindData)
        }

        override fun onBindViewHolder(holder: VH<T>, position: Int) {
            holder.bind(getItem(position))
        }

    }
    layoutManager = layout
    adapter = listAdapter
    scope.launch {
        item.collect {
            listAdapter.submitList(mutableListOf<T>().apply { addAll(it) })
        }
    }
}

/**
 * ViewHolder
 * @param T : Any 数据类型
 * @property variableId Int DataBinding的id，ViewBinding可随意传
 * @property view View 布局
 * @property bindData Function2<View, T, Unit> 自实现布局渲染
 * @constructor
 */
class VH<T : Any> constructor(
    val variableId: Int,
    val view: View,
    val bindData: (View, T) -> Unit,
) : RecyclerView.ViewHolder(view) {

    fun bind(t: T) {
        DataBindingUtil.findBinding<ViewDataBinding?>(view)?.apply {
            setVariable(variableId, t)
        } ?: run {
            bindData.invoke(view, t)
        }
    }
}

/**
 * 布局
 * @param T : Any 数据类型
 * @property variableId Int DataBinding的id，ViewBinding可随意传
 * @property layoutId Int 布局id
 * @property bindData Function2<View, T, Unit> 自实现布局渲染
 * @constructor
 */
data class ItemBinding<T : Any> @JvmOverloads constructor(
    val variableId: Int,
    val layoutId: Int,
    val bindData: (View, T) -> Unit = { _, _ -> },
)

/**
 * LinearLayoutManager
 * @receiver RecyclerView
 * @param orientation Int
 * @param reverseLayout Boolean
 * @return LayoutManager
 */
@JvmOverloads
fun RecyclerView.linear(
    orientation: Int = RecyclerView.VERTICAL,
    reverseLayout: Boolean = false,
): LayoutManager {
    return LinearLayoutManager(context, orientation, reverseLayout)
}

/**
 * GridLayoutManager
 * @receiver RecyclerView
 * @param spanCount Int
 * @param orientation Int
 * @param reverseLayout Boolean
 * @return LayoutManager
 */
@JvmOverloads
fun RecyclerView.grid(
    spanCount: Int,
    orientation: Int = RecyclerView.VERTICAL,
    reverseLayout: Boolean = false,
): LayoutManager {
    return GridLayoutManager(context, spanCount, orientation, reverseLayout)
}