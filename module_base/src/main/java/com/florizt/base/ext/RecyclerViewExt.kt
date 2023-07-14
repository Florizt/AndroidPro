package com.florizt.base.ext

import android.view.LayoutInflater
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

inline fun <reified T : Any, reified VB : ViewBinding> RecyclerView.bind(
    scope: CoroutineScope,
    crossinline areItemsTheSame: (T, T) -> Boolean,
    crossinline areContentsTheSame: (T, T) -> Boolean,
    layout: LayoutManager,
    item: Flow<MutableList<T>>,
    itemBinding: ItemBinding<T, VB>,
) {
    val listAdapter = object : ListAdapter<T, VH<T, VB>>(
        object : DiffUtil.ItemCallback<T>() {
            override fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
                return areItemsTheSame.invoke(oldItem, newItem)
            }

            override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
                return areContentsTheSame.invoke(oldItem, newItem)
            }

        }
    ) {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH<T, VB> {
            return VH(
                itemBinding.variableId!!,
                DataBindingUtil.inflate(
                    LayoutInflater.from(context),
                    itemBinding.layoutId!!, null, false
                ) ?: VB::class.java.getMethod("inflate", LayoutInflater::class.java).run {
                    invoke(null, LayoutInflater.from(context)) as VB
                },
                itemBinding.bindData
            )
        }

        override fun onBindViewHolder(holder: VH<T, VB>, position: Int) {
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

inline fun <reified T : Any, reified VB : ViewBinding> RecyclerView.bindPaging(
    scope: CoroutineScope,
    crossinline areItemsTheSame: (T, T) -> Boolean,
    crossinline areContentsTheSame: (T, T) -> Boolean,
    layout: LayoutManager,
    item: Flow<PagingData<T>>,
    itemBinding: ItemBinding<T, VB>,
) {
    val listAdapter = object : PagingDataAdapter<T, VH<T, VB>>(
        object : DiffUtil.ItemCallback<T>() {
            override fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
                return areItemsTheSame.invoke(oldItem, newItem)
            }

            override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
                return areContentsTheSame.invoke(oldItem, newItem)
            }

        }
    ) {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH<T, VB> {
            return VH(
                itemBinding.variableId!!,
                DataBindingUtil.inflate(
                    LayoutInflater.from(context),
                    itemBinding.layoutId!!, null, false
                ) ?: VB::class.java.getMethod("inflate", LayoutInflater::class.java).run {
                    invoke(null, LayoutInflater.from(context)) as VB
                },
                itemBinding.bindData
            )
        }

        override fun onBindViewHolder(holder: VH<T, VB>, position: Int) {
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

class VH<T : Any, VB : ViewBinding> @JvmOverloads constructor(
    val variableId: Int,
    val vb: VB,
    var bindData: ((T, VB) -> Unit)? = null,
) : RecyclerView.ViewHolder(vb.root) {
    fun bind(t: T) {
        if (vb is ViewDataBinding) {
            vb.setVariable(variableId, t)
        } else {
            bindData?.invoke(t, vb)
        }
    }
}

class ItemBinding<T : Any, VB : ViewBinding> private constructor() {
    var variableId: Int? = null
    var layoutId: Int? = null
    var bindData: ((T, VB) -> Unit)? = null

    @JvmOverloads
    constructor(variableId: Int, layoutId: Int, bindData: ((T, VB) -> Unit)? = null) : this() {
        this.variableId = variableId
        this.layoutId = layoutId
        this.bindData = bindData
    }

    companion object {
        @JvmStatic
        @JvmOverloads
        fun <T : Any, VB : ViewBinding> of(
            variableId: Int,
            layoutId: Int,
            bindData: ((T, VB) -> Unit)? = null,
        ): ItemBinding<T, VB> {
            return ItemBinding(variableId, layoutId, bindData)
        }
    }
}

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