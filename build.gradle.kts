/**
 * 相当于项目根目录 build.gradle 中的 buildscript{}#dependencies{}
 */
plugins {
    /**
     * 用 apply false来添加其作为 build dependency，但是不应用到root project中
     * 在子项目中，需要去掉apply false
     */
    //相当于[classpath 'com.android.tools.build:gradle:7.4.2']
    id("com.android.application").version("7.4.2").apply(false)
    id("com.android.library").version("7.4.2").apply(false)
    //相当于[classpath "org.jetbrains.kotlin:kotlin-gradle-plugin:1.8.0"]
    kotlin("android").version("1.8.0").apply(false)
}