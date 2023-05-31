package com.florizt.androidpro

import android.os.Bundle
import android.os.Parcelable
import androidx.activity.ComponentActivity
import com.florizt.androidpro.databinding.ActivityMainBinding
import com.florizt.base.delegate.viewBinding
import com.florizt.base.delegate.viewModelsLifecycle
import com.florizt.base.repository.cache.mmkvParcelable
import com.florizt.base.repository.cache.sharedPreference
import com.florizt.base.repository.cache.sharedPreferenceSerializable
import com.florizt.base.app.ImmersionBars
import com.florizt.base.app.initImmersionBar
import kotlinx.parcelize.Parcelize
import java.io.Serializable

@ImmersionBars
class MainActivity : ComponentActivity() {

    var aaa by sharedPreference<Boolean>()
    var user by sharedPreferenceSerializable<User>()
    var teach by mmkvParcelable<Teach>()

    private val binding by viewBinding<ActivityMainBinding>()
    private val viewModel by viewModelsLifecycle<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        println("============== 111 $binding")
        println("============== 222 $viewModel")
        println("============== 333 $teach")
        println("============== 444 $user")
        user = User("sasa",111)
        teach = Teach("wqdsadas",222)
        println("============== 555 $teach")
        println("============== 666 $user")

        initImmersionBar { isPopup, keyboardHeight ->
            println("===============  $isPopup  $keyboardHeight")
        }
    }
}

@kotlinx.serialization.Serializable
data class User(val name: String, val age: Int) : Serializable

@Parcelize
data class Teach(val name: String, val age: Int) : Parcelable