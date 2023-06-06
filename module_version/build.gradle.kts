plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    google()
}

dependencies {
    compileOnly("com.android.tools.build:gradle:7.4.2")
    compileOnly("org.jetbrains.kotlin:kotlin-gradle-plugin:1.8.0")
    compileOnly("org.ow2.asm:asm:9.3")
    compileOnly("org.ow2.asm:asm-commons:9.3")
}

gradlePlugin {
    plugins {
        create("versionPlugin") {
            //自定义plugin的id，其他module引用要用到
            id = "com.florizt.version"
            //指向自定义plugin类，因为直接放在目录下，所以没有多余路径
            implementationClass = "VersionPlugin"
        }
    }
}