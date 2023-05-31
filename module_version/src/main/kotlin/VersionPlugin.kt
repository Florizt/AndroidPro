@file:JvmName("VersionPluginKt")

import com.android.build.gradle.*
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.PluginContainer
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.plugin.getKotlinPluginVersion
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

const val api = "api"
const val implementation = "implementation"
const val kapt = "kapt"

class VersionPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.plugins.config(project)
    }

    private fun PluginContainer.config(project: Project) {
       if(project.plugins.hasPlugin(AppPlugin::class.java)){
           project.plugins.getPlugin(AppPlugin::class.java).apply {
               //公共插件
               project.configPlugin()
               //公共 android 配置项
               project.extensions.getByType<AppExtension>().configBuilds(project)
               Kotlin.kotlin_version = project.getKotlinPluginVersion()
           }
       }

        if(project.plugins.hasPlugin(LibraryPlugin::class.java)){
            project.plugins.getPlugin(LibraryPlugin::class.java).apply {
                //公共插件
                project.configPlugin()
                //公共 android 配置项
                project.extensions.getByType<LibraryExtension>().configBuilds(project)
                //公共依赖
                project.configLibraryDependencies()
                Kotlin.kotlin_version = project.getKotlinPluginVersion()
            }
        }
    }


    /**
     * 公共plugin插件依赖
     */
    private fun Project.configPlugin() {
        plugins.apply("kotlin-android")
        plugins.apply("kotlin-kapt")
        plugins.apply("kotlin-parcelize")
    }

    /**
     * 公共需要添加的设置，如sdk目标版本，jdk版本，jvm目标版本等
     */
    private fun BaseExtension.configBuilds(project: Project) {
        compileSdkVersion(BuildConfig.compileSdkVersion)

        defaultConfig {
            minSdk = BuildConfig.minSdkVersion
            targetSdk = BuildConfig.targetSdkVersion
        }

        dataBinding {
            enable = true
        }

        viewBinding {
            enable = true
        }

        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_1_8
            targetCompatibility = JavaVersion.VERSION_1_8
        }

        project.tasks.withType<KotlinCompile> {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
    }

    /**
     * library Module 公共依赖
     */
    private fun Project.configLibraryDependencies() {
        dependencies.apply {
            add(api, fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
            add(api, Kotlin.stdlib)
            add(api, Kotlin.serialization)
            add(api, Kotlin.Coroutines.core)
            add(api, Kotlin.Coroutines.android)
            add(api, AndroidX.appCompat)
            add(api, AndroidX.startup)
            add(api, AndroidX.cardview)
            add(api, AndroidX.recyclerView)
            add(api, AndroidX.recyclerView_selection)
            add(api, AndroidX.constraintLayout)
            add(api, AndroidX.drawerLayout)
            add(api, AndroidX.swipeRefreshLayout)
            add(api, AndroidX.viewPager2)
            add(api, AndroidX.Core.ktx)
            add(api, AndroidX.Activity.ktx)
            add(api, AndroidX.Fragment.ktx)
            add(api, AndroidX.Lifecycle.runtime)
            add(api, AndroidX.Lifecycle.viewmodel)
            add(api, AndroidX.Lifecycle.jdk8)
            add(api, AndroidX.Lifecycle.savedstate)
            add(api, AndroidX.Lifecycle.process)
            add(api, AndroidX.Room.runtime)
            add(api, AndroidX.Room.compiler)
            add(api, AndroidX.Room.ktx)
            add(api, AndroidX.Room.paging)
            add(api, AndroidX.Paging.runtime)
            add(api, AndroidX.Work.runtime)
            add(api, AndroidX.DataStore.core)
            add(api, AndroidX.DataStore.preferences)
            add(api, Google.material)
            add(api, Google.gson)
            add(api, Square.OkHttp.okhttp)
            add(api, Square.OkHttp.logging)
            add(api, Square.OkHttp.urlConnection)
            add(api, Square.Retrofit.retrofit)
            add(api, Square.Retrofit.scalarsConverter)
            add(api, Square.Retrofit.gsonConverter)
            add(api, ThirdParty.glide)
            add(kapt, ThirdParty.glideCompiler)
            add(api, ThirdParty.immersionbar)
            add(api, ThirdParty.autosize)
            add(api, ThirdParty.backgroundLibrary)
            add(api, ThirdParty.mmkv)
            add(api, ThirdParty.logx)
        }
    }
}