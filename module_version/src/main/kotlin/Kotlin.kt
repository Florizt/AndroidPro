/**
 * Created by wuwei
 * 2023/5/26
 * 佛祖保佑       永无BUG
 * desc：
 */
object Kotlin {
    var kotlin_version = "1.4.20"

    val stdlib = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlin_version"
    val serialization = "org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.1"

    object Coroutines {
        private const val coroutine_version = "1.6.2"
        const val core = "org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutine_version"
        const val android = "org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutine_version"
    }
}