package com.florizt.base.ext

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.paging.PagingData
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import androidx.viewbinding.ViewBinding
import androidx.viewpager2.widget.ViewPager2
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

inline fun <reified T : Any> RecyclerView.bind(
    scope: CoroutineScope,
    crossinline areItemsTheSame: (T, T) -> Boolean,
    crossinline areContentsTheSame: (T, T) -> Boolean,
    layout: LayoutManager,
    item: Flow<MutableList<T>>,
    crossinline itemBinding: (Int, T) -> ItemBinding,
) {
    val itemBindings: MutableMap<Int, ItemBinding> = mutableMapOf()

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
        override fun getItemViewType(position: Int): Int {
            return itemBinding.invoke(position, getItem(position)).run {
                hashCode().also {
                    itemBindings.put(it, this)
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH<T> {
            return itemBindings.get(viewType)?.run {
                val view = DataBindingUtil.inflate<ViewDataBinding?>(
                    LayoutInflater.from(context),
                    layoutId, null, false
                )?.root ?: View.inflate(context, layoutId, null)

                VH(variableId, view, bindData)
            } ?: error("itemBinding init error")
        }

        override fun onBindViewHolder(holder: VH<T>, position: Int) {
            holder.bind(getItem(position))
        }

    }
    layoutManager = layout
    adapter = listAdapter
    scope.launch {
        item.collect {
            listAdapter.submitList(it)
        }
    }
}

inline fun <reified T : Any> RecyclerView.bindPaging(
    scope: CoroutineScope,
    crossinline areItemsTheSame: (T, T) -> Boolean,
    crossinline areContentsTheSame: (T, T) -> Boolean,
    layout: LayoutManager,
    item: Flow<PagingData<T>>,
    crossinline itemBinding: (Int, T) -> ItemBinding,
) {
    val itemBindings: MutableMap<Int, ItemBinding> = mutableMapOf()

    val listAdapter = object : PagingDataAdapter<T, VH<T>>(
        object : DiffUtil.ItemCallback<T>() {
            override fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
                return areItemsTheSame.invoke(oldItem, newItem)
            }

            override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
                return areContentsTheSame.invoke(oldItem, newItem)
            }

        }
    ) {
        override fun getItemViewType(position: Int): Int {
            return getItem(position)?.run {
                itemBinding.invoke(position, this).run {
                    hashCode().also {
                        itemBindings.put(it, this)
                    }
                }
            } ?: error("item is null")
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH<T> {
            return itemBindings.get(viewType)?.run {
                val view = DataBindingUtil.inflate<ViewDataBinding?>(
                    LayoutInflater.from(context),
                    layoutId, null, false
                )?.root ?: View.inflate(context, layoutId, null)

                VH(variableId, view, bindData)
            } ?: error("itemBinding init error")
        }

        override fun onBindViewHolder(holder: VH<T>, position: Int) {
            getItem(position)?.let { holder.bind(it) }
        }

    }
    layoutManager = layout
    adapter = listAdapter
    scope.launch {
        item.collect {
            listAdapter.submitData(it)
        }
    }
}

class VH<T : Any> constructor(
    val variableId: Int,
    val view: View,
    val bindData: (View) -> Unit,
) : RecyclerView.ViewHolder(view) {

    fun bind(t: T) {
        DataBindingUtil.findBinding<ViewDataBinding?>(view)?.apply {
            setVariable(variableId, t)
        } ?: run {
            bindData.invoke(view)
        }
    }
}

data class ItemBinding @JvmOverloads constructor(
    val variableId: Int,
    val layoutId: Int,
    val bindData: (View) -> Unit = {},
)

@JvmOverloads
fun RecyclerView.linear(
    orientation: Int = RecyclerView.VERTICAL,
    reverseLayout: Boolean = false,
): LayoutManager {
    return LinearLayoutManager(context, orientation, reverseLayout)
}

@JvmOverloads
fun RecyclerView.grid(
    spanCount: Int,
    orientation: Int = RecyclerView.VERTICAL,
    reverseLayout: Boolean = false,
): LayoutManager {
    return GridLayoutManager(context, spanCount, orientation, reverseLayout)
}