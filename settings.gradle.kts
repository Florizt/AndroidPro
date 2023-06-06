/**
 * 插件管理，相当于项目根目录 build.gradle 中的 buildscript{}#repositories{}
 * 里面是gradle脚本执行所需插件依赖
 * buildscript{}：repositories{}(插件所依赖的仓库) + dependencies{}(插件)
 * 现将 repositories{} 部分移至项目根目录 settings.gradle 中的 pluginManagement{}
 * 而 dependencies{} 部分依旧放在项目根目录 build.gradle 中并改名为 plugins{}
 */
pluginManagement {
    repositories {
        google()
        jcenter()
        mavenCentral()
        gradlePluginPortal()
        maven("https://jitpack.io")
    }
}
/**
 * 里面是项目本身需要的依赖
 * 相当于项目根目录 build.gradle 中的 allprojects{}
 */
dependencyResolutionManagement {
    /**
     * FAIL_ON_PROJECT_REPOS
     * 表示如果工程单独设置了仓库，或工程的插件设置了仓库，构建就直接报错抛出异常
     * PREFER_PROJECT
     * 表示如果工程单独设置了仓库，就优先使用工程配置的，忽略settings里面的
     * PREFER_SETTINGS
     * 表述任何通过工程单独设置或插件设置的仓库，都会被忽略
     */
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        //有些框架没迁移，不能删
        jcenter()
        mavenCentral()
        maven("https://jitpack.io")
    }
}
rootProject.name = "AndroidPro"
include(":app")
includeBuild("module_version")
include(":module_base")
