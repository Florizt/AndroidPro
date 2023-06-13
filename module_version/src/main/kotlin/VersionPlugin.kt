@file:JvmName("VersionPluginKt")

import com.android.build.gradle.*
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.PluginContainer
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.plugin.getKotlinPluginVersion
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

const val implementation = "implementation"
const val api = "api"
const val kapt = "kapt"

class VersionPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.plugins.config(project)
    }

    private fun PluginContainer.config(project: Project) {
        if (project.plugins.hasPlugin(AppPlugin::class.java)) {
            project.plugins.getPlugin(AppPlugin::class.java).apply {
                configs(project, project.extensions.getByType<AppExtension>())
            }
        }

        if (project.plugins.hasPlugin(LibraryPlugin::class.java)) {
            project.plugins.getPlugin(LibraryPlugin::class.java).apply {
                configs(project, project.extensions.getByType<LibraryExtension>())
            }
        }
    }

    private fun configs(project: Project, extension: BaseExtension) {
        //公共插件
        project.configPlugin()
        //公共 android 配置项
        extension.configBuilds(project)
        //公共依赖
        project.configLibraryDependencies()
        Kotlin.kotlin_version = project.getKotlinPluginVersion()
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
            add(implementation, fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
            add(implementation, Kotlin.stdlib)
            add(implementation, Kotlin.serialization)
            add(implementation, Kotlin.Coroutines.core)
            add(implementation, Kotlin.Coroutines.android)
            add(implementation, AndroidX.appCompat)
            add(implementation, AndroidX.startup)
            add(implementation, AndroidX.cardview)
            add(implementation, AndroidX.recyclerView)
            add(implementation, AndroidX.recyclerView_selection)
            add(implementation, AndroidX.constraintLayout)
            add(implementation, AndroidX.drawerLayout)
            add(implementation, AndroidX.swipeRefreshLayout)
            add(implementation, AndroidX.viewPager2)
            add(implementation, AndroidX.Core.ktx)
            add(implementation, AndroidX.Activity.ktx)
            add(implementation, AndroidX.Fragment.ktx)
            add(implementation, AndroidX.Lifecycle.runtime)
            add(implementation, AndroidX.Lifecycle.viewmodel)
            add(implementation, AndroidX.Lifecycle.jdk8)
            add(implementation, AndroidX.Lifecycle.savedstate)
            add(implementation, AndroidX.Lifecycle.process)
            add(implementation, AndroidX.Room.runtime)
            add(implementation, AndroidX.Room.compiler)
            add(implementation, AndroidX.Room.ktx)
            add(implementation, AndroidX.Room.paging)
            add(implementation, AndroidX.Paging.runtime)
            add(implementation, AndroidX.Work.runtime)
            add(implementation, AndroidX.DataStore.core)
            add(implementation, AndroidX.DataStore.preferences)
            add(implementation, Google.material)
            add(implementation, Google.gson)
            add(implementation, Square.OkHttp.okhttp)
            add(implementation, Square.OkHttp.logging)
            add(implementation, Square.OkHttp.urlConnection)
            add(implementation, Square.Retrofit.retrofit)
            add(implementation, Square.Retrofit.scalarsConverter)
            add(implementation, Square.Retrofit.gsonConverter)
            add(implementation, ThirdParty.glide)
            add(kapt, ThirdParty.glideCompiler)
            add(implementation, ThirdParty.immersionbar)
            add(implementation, ThirdParty.immersionbarKtx)
            add(implementation, ThirdParty.autosize)
            add(implementation, ThirdParty.backgroundLibrary)
            add(implementation, ThirdParty.mmkv)
            add(implementation, ThirdParty.logx)
        }
    }
}