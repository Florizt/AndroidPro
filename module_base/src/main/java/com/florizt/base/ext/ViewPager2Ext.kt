@file:JvmName("ViewPager2Ext")

package com.florizt.base.ext

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.Orientation
import androidx.viewpager2.widget.ViewPager2.PageTransformer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

/**
 * ViewPager2快速绑定，减少Adapter创建
 * @receiver ViewPager2
 * @param scope CoroutineScope 生命周期域
 * @param orientation Int 方向
 * @param offscreenPageLimit Int 缓存个数
 * @param pageTransformer PageTransformer
 * @param areItemsTheSame Function2<T, T, Boolean> 是否同一个item
 * @param areContentsTheSame Function2<T, T, Boolean> 是否同一个content
 * @param item Flow<MutableList<T>> 数据
 * @param itemBinding ItemBinding<T> 布局
 */
@JvmOverloads
inline fun <reified T : Any> ViewPager2.bind(
    scope: CoroutineScope,
    @Orientation orientation: Int = ViewPager2.ORIENTATION_VERTICAL,
    offscreenPageLimit: Int = 2,
    pageTransformer: PageTransformer = object : PageTransformer {
        override fun transformPage(page: View, position: Float) {
        }
    },
    crossinline areItemsTheSame: (T, T) -> Boolean,
    crossinline areContentsTheSame: (T, T) -> Boolean,
    item: Flow<MutableList<T>>,
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
    this.orientation = orientation
    this.offscreenPageLimit = offscreenPageLimit
    setPageTransformer(pageTransformer)
    adapter = listAdapter
    scope.launch {
        item.collect {
            listAdapter.submitList(it)
        }
    }
}

/**
 * ViewPager2快速绑定Fragment，减少Adapter创建
 * @receiver ViewPager2
 * @param fragmentActivity FragmentActivity
 * @param orientation Int 方向
 * @param offscreenPageLimit Int 缓存个数
 * @param pageTransformer PageTransformer
 * @param item MutableList<T> 数据
 * @param itemBinding Function2<Int, T, Fragment> 布局
 */
@JvmOverloads
inline fun <reified T : Any> ViewPager2.bind(
    fragmentActivity: FragmentActivity,
    @Orientation orientation: Int = ViewPager2.ORIENTATION_VERTICAL,
    offscreenPageLimit: Int = 2,
    pageTransformer: PageTransformer = object : PageTransformer {
        override fun transformPage(page: View, position: Float) {
        }
    },
    item: MutableList<T>,
    crossinline itemBinding: (Int, T) -> Fragment,
) {
    this.orientation = orientation
    this.offscreenPageLimit = offscreenPageLimit
    setPageTransformer(pageTransformer)
    adapter = object : FragmentStateAdapter(fragmentActivity) {
        override fun getItemCount(): Int {
            return item.size
        }

        override fun createFragment(position: Int): Fragment {
            return itemBinding.invoke(position, item.get(position))
        }
    }
}