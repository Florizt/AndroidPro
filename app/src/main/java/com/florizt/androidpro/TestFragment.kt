package com.florizt.androidpro

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.florizt.androidpro.databinding.TestBinding
import com.florizt.base.delegate.argument

/**
 * Created by wuwei
 * 2023/6/9
 * 佛祖保佑       永无BUG
 * desc：
 */
class TestFragment : Fragment() {
    var position by argument<Int>()

    companion object {
        fun instance(p: Int): TestFragment {
            return TestFragment().apply {
                position = p
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        println("------------ onCreate $position")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        println("------------ onCreateView $position")
        return TestBinding.inflate(inflater).apply {
            tv.text = "$position"
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        println("------------ onViewCreated $position")
    }

    override fun onStart() {
        super.onStart()
        println("------------ onStart $position")
    }

    override fun onResume() {
        super.onResume()
        println("------------ onResume $position")
    }

    override fun onPause() {
        super.onPause()
        println("------------ onPause $position")
    }

    override fun onStop() {
        super.onStop()
        println("------------ onStop $position")
    }

    override fun onDestroy() {
        super.onDestroy()
        println("------------ onDestroy $position")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        println("------------ onDestroyView $position")
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        println("------------ onAttach $position")
    }

    override fun onDetach() {
        super.onDetach()
        println("------------ onDetach $position")
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        println("------------ onHiddenChanged $position")
    }
}