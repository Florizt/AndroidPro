package com.florizt.androidpro

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.florizt.androidpro.databinding.ActivityMainBinding
import com.florizt.base.delegate.intent
import com.florizt.base.delegate.viewBinding

/**
 * Created by wuwei
 * 2023/6/12
 * 佛祖保佑       永无BUG
 * desc：
 */
class TestActivity : AppCompatActivity() {
    private val binding by viewBinding<ActivityMainBinding>()
    private var aaa by intent<Int>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding
        aaa.apply {

        }

        println("================ 111 $aaa")
        aaa= 9999
        println("================ 222 $aaa")
    }
}