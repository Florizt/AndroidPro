plugins {
    id("com.android.application")
    id("com.florizt.version")
    kotlin("plugin.serialization").version("1.6.10")
}

android {
    namespace = "com.florizt.androidpro"

    defaultConfig {
        applicationId= "com.florizt.androidpro"
        versionCode =1
        versionName ="1.0"
    }

    configurations{
        implementation.get().exclude(group = "com.intellij",module = "annotations")
    }
}

dependencies {
    implementation(project(":module_base"))
}