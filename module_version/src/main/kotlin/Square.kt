
object Square {
    object OkHttp {
        private const val okhttp_version = "3.14.9"
        const val okhttp = "com.squareup.okhttp3:okhttp:$okhttp_version"
        const val logging = "com.squareup.okhttp3:logging-interceptor:$okhttp_version"
        const val urlConnection = "com.squareup.okhttp3:okhttp-urlconnection:$okhttp_version"
    }

    object Retrofit {
        private const val retrofit_version = "2.9.0"
        const val retrofit = "com.squareup.retrofit2:retrofit:$retrofit_version"
        const val scalarsConverter = "com.squareup.retrofit2:converter-scalars:$retrofit_version"
        const val gsonConverter = "com.squareup.retrofit2:converter-gson:$retrofit_version"
    }
}