package com.florizt.base.repository.cache

import android.content.Context
import android.os.Build
import android.os.Environment
import android.os.StatFs
import java.io.*
import java.text.DecimalFormat
import java.util.*


enum class FileType {
    TYPE_IMAGE,
    TYPE_VIDEO,
    TYPE_AUDIO,
    TYPE_DOWNLOAD,
    TYPE_DOCUMENT
}

/**
 * 创建文件
 * @receiver Context
 * @param type Int 文件类型，对应不同的文件路径
 * @param fileName String 必须是包含后缀的完整文件名
 * @return File?
 */
fun Context.createFile(type: FileType, fileName: String): File {
    return File(createDir(type), fileName).apply {
        if (!exists()) createNewFile()
    }
}

private fun Context.createDir(type: FileType): File {
    val rootDir =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            getRootDirFile(type)
        else
            if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED)
                Environment.getExternalStorageDirectory()
            else
                cacheDir

    return rootDir?.apply {
        if (!exists()) mkdirs()
    }?.run {
        File(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                getAbsolutePath().toString()
            else
                getAbsolutePath().toString() + getParentPath(type)
        )
    }?.apply {
        if (!exists()) mkdirs()
    } ?: run {
        error("createDir failed")
    }
}

private fun Context.getRootDirFile(type: FileType): File? {
    return when (type) {
        FileType.TYPE_VIDEO -> getExternalFilesDir(Environment.DIRECTORY_MOVIES)
        FileType.TYPE_AUDIO -> getExternalFilesDir(Environment.DIRECTORY_MUSIC)
        FileType.TYPE_IMAGE -> getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        FileType.TYPE_DOWNLOAD -> getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
        FileType.TYPE_DOCUMENT -> getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
    }
}

private fun Context.getParentPath(type: FileType): String {
    return when (type) {
        FileType.TYPE_VIDEO -> "/$packageName/video/"
        FileType.TYPE_AUDIO -> "/$packageName/audio/"
        FileType.TYPE_IMAGE -> "/$packageName/image/"
        FileType.TYPE_DOWNLOAD -> "/$packageName/download/"
        FileType.TYPE_DOCUMENT -> "/$packageName/document/"
    }
}