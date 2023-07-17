package com.florizt.androidpro

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.paging.PagedListAdapter
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.florizt.androidpro.databinding.ActivityMainBinding
import com.florizt.androidpro.databinding.ItemMainBinding
import com.florizt.base.delegate.viewBinding
import com.florizt.base.delegate.viewModelsLifecycle
import com.florizt.base.ext.ItemBinding
import com.florizt.base.ext.bind
import com.florizt.base.ext.linear
import com.florizt.base.ui.ImmersionBars
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@ImmersionBars
class MainActivity : AppCompatActivity() {

    private val binding by viewBinding<ActivityMainBinding>()
    private val viewModel by viewModelsLifecycle<MainViewModel>()

    private val list = MutableSharedFlow<MutableList<String>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.rec.apply {
            bind(
                scope = lifecycleScope,
                areItemsTheSame = { oldItem, newItem ->
                    oldItem.hashCode() == newItem.hashCode()
                },
                areContentsTheSame = { oldItem, newItem ->
                    oldItem === newItem
                },
                layout = linear(),
                item = list,
                itemBinding = { position, data ->
                    if (position % 2 == 0) {
                        ItemBinding(BR.data, R.layout.item_main)
                    } else {
                        ItemBinding(BR.data, R.layout.item_main2,{
                            it.findViewById<TextView>(R.id.text).text = data
                        })
                    }
                }
            )
        }

        lifecycleScope.launch {
            val l = mutableListOf<String>()
            for (i in 0..10) {
                l.add("$i")
            }
            list.emit(l)
        }
    }
}