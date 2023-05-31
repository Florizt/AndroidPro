/**
 * Created by wuwei
 * 2023/5/26
 * 佛祖保佑       永无BUG
 * desc：
 */
object AndroidX {
    const val appCompat = "androidx.appcompat:appcompat:1.5.1"
    const val startup = "androidx.startup:startup-runtime:1.0.0"
    const val constraintLayout = "androidx.constraintlayout:constraintlayout:2.1.2"
    const val recyclerView = "androidx.recyclerview:recyclerview:1.2.1"
    const val recyclerView_selection = "androidx.recyclerview:recyclerview-selection:1.1.0"
    const val drawerLayout = "androidx.drawerlayout:drawerlayout:1.1.1"
    const val swipeRefreshLayout = "androidx.swiperefreshlayout:swiperefreshlayout:1.2.0-alpha01"
    const val viewPager2 = "androidx.viewpager2:viewpager2:1.1.0-beta01"
    const val cardview = "androidx.cardview:cardview:1.0.0"

    object Core {
        const val ktx = "androidx.core:core-ktx:1.7.0"
    }

    object Activity {
        const val ktx = "androidx.activity:activity-ktx:1.4.0"
    }

    object Fragment {
        const val ktx = "androidx.fragment:fragment-ktx:1.4.1"
    }

    object Lifecycle {
        private const val lifecycle_version = "2.4.1"
        const val runtime = "androidx.lifecycle:lifecycle-runtime-ktx:$lifecycle_version"
        const val viewmodel = "androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycle_version"
        const val jdk8 = "androidx.lifecycle:lifecycle-common-java8:$lifecycle_version"
        const val savedstate = "androidx.lifecycle:lifecycle-viewmodel-savedstate:$lifecycle_version"
        const val process = "androidx.lifecycle:lifecycle-process:$lifecycle_version"
    }

    object Room {
        private const val room_version = "2.4.2"
        const val runtime = "androidx.room:room-runtime:$room_version"
        const val compiler = "androidx.room:room-compiler:$room_version"
        const val ktx = "androidx.room:room-ktx:$room_version"
        const val paging = "androidx.room:room-paging:$room_version"
    }

    object Paging {
        private const val paging_version = "3.1.1"
        const val runtime = "androidx.paging:paging-runtime-ktx:$paging_version"
    }

    object Work {
        private const val work_version = "2.7.1"
        const val runtime = "androidx.work:work-runtime-ktx:$work_version"
    }

    object DataStore {
        private const val datasource_version = "1.0.0"
        const val core = "androidx.datastore:datastore-core:$datasource_version"
        const val preferences = "androidx.datastore:datastore-preferences:$datasource_version"
    }
}