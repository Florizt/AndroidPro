object Kotlin {
    var kotlin_version = "1.8.0"

    val stdlib = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlin_version"

    object Coroutines {
        private const val coroutine_version = "1.6.2"
        const val core = "org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutine_version"
        const val android = "org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutine_version"
    }
}