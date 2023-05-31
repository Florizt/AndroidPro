package com.florizt.base.repository.cache

import android.content.Context
import android.os.Build
import android.os.Environment
import android.os.StatFs
import java.io.*
import java.text.DecimalFormat
import java.util.*


const val TYPE_IMAGE = 1
const val TYPE_VIDEO = 2
const val TYPE_AUDIO = 3
const val TYPE_DOWNLOAD = 4
const val TYPE_DOCUMENT = 5

/**
 * 创建文件
 * @receiver Context
 * @param type Int 文件类型，对应不同的文件路径
 * @param fileName String 必须是包含后缀的完整文件名
 * @param subFolder String? 文件子路径
 * @return File?
 */
fun Context.createFile(
    type: Int,
    fileName: String,
    subFolder: String? = null
): File {
    return File(createDir(type, subFolder), fileName).also {
        if (!it.exists()) {
            it.createNewFile()
        }
    }
}

private fun Context.createDir(type: Int, subFolder: String? = null): File {
    var sub = subFolder
    if (sub != null && sub.startsWith("/") && sub.length > 1) {
        sub = sub.substring(1, sub.length)
    } else {
        sub = ""
    }
    val rootDir: File?
    rootDir = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        getRootDirFile(type)
    } else {
        val state: String = Environment.getExternalStorageState()
        if (state == Environment.MEDIA_MOUNTED) Environment.getExternalStorageDirectory() else cacheDir
    }
    return rootDir?.also {
        if (!it.exists()) {
            it.mkdirs()
        }
    }?.let {
        File(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                it.getAbsolutePath().toString() + "/" + sub
            else
                it.getAbsolutePath().toString() + getParentPath(type, sub)
        )
    }?.also {
        if (!it.exists()) {
            it.mkdirs()
        }
    } ?: run {
        throw IllegalArgumentException()
    }
}

private fun Context.getRootDirFile(type: Int): File? {
    return when (type) {
        TYPE_VIDEO -> getExternalFilesDir(Environment.DIRECTORY_MOVIES)
        TYPE_AUDIO -> getExternalFilesDir(Environment.DIRECTORY_MUSIC)
        TYPE_IMAGE -> getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        TYPE_DOWNLOAD -> getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
        TYPE_DOCUMENT -> getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
        else -> getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
    }
}

private fun Context.getParentPath(
    type: Int,
    subFolder: String?
): String {
    var sub = subFolder
    if (sub != null && sub.startsWith("/") && sub.length > 1) {
        sub = sub.substring(1, sub.length)
    } else {
        sub = ""
    }
    return when (type) {
        TYPE_VIDEO -> "/" + packageName + "/video/" + sub
        TYPE_AUDIO -> "/" + packageName + "/audio/" + sub
        TYPE_IMAGE -> "/" + packageName + "/image/" + sub
        TYPE_DOWNLOAD -> "/" + packageName + "/download/" + sub
        TYPE_DOCUMENT -> "/" + packageName + "/document/" + sub
        else -> "/" + packageName + "/download/" + sub
    }
}

const val SIZETYPE_B = 1 //获取文件大小单位为B的double值
const val SIZETYPE_KB = 2 //获取文件大小单位为KB的double值
const val SIZETYPE_MB = 3 //获取文件大小单位为MB的double值
const val SIZETYPE_GB = 4 //获取文件大小单位为GB的double值

fun File.getROMAvailableSize(): Long {
    val statFs = StatFs(path)
    return statFs.blockSizeLong * statFs.availableBlocksLong
}

/**
 * 获取文件的大小
 *
 * @param file 文件
 * @return String值的大小
 */
fun File.getFileSize(): String {
    var blockSize: Long = 0
    try {
        blockSize = if (isDirectory) {
            this.getSizes()
        } else {
            this.getSize()
        }
    } catch (e: java.lang.Exception) {
        e.printStackTrace()
    }
    return FormatFileSize(blockSize)
}

/**
 * 获取文件的指定单位的大小
 *
 * @param file     文件
 * @param sizeType 获取大小的类型1为B、2为KB、3为MB、4为GB
 * @return double值的大小
 */
fun File.getFileSize(sizeType: Int): Double {
    var blockSize: Long = 0
    try {
        blockSize = if (isDirectory) {
            this.getSizes()
        } else {
            this.getSize()
        }
    } catch (e: java.lang.Exception) {
        e.printStackTrace()
    }
    return FormatFileSize(blockSize, sizeType)
}

/**
 * 获取指定文件大小
 *
 * @param
 * @return
 * @throws Exception
 */
@Throws(java.lang.Exception::class)
private fun File.getSize(): Long {
    var size: Long = 0
    if (exists()) {
        var fis: FileInputStream? = null
        fis = FileInputStream(this)
        size = fis.available().toLong()
    } else {
        this.createNewFile()
    }
    return size
}

/**
 * 获取指定文件夹
 *
 * @param f
 * @return
 * @throws Exception
 */
@Throws(java.lang.Exception::class)
private fun File.getSizes(): Long {
    var size: Long = 0
    val flist = listFiles()
    for (i in flist.indices) {
        size = if (flist[i].isDirectory) {
            size + flist[i].getSizes()
        } else {
            size + flist[i].getSize()
        }
    }
    return size
}

/**
 * 转换文件大小
 *
 * @param fileS
 * @return
 */
private fun FormatFileSize(fileS: Long): String {
    val df = DecimalFormat("#.00")
    var fileSizeString = ""
    val wrongSize = "0B"
    if (fileS == 0L) {
        return wrongSize
    }
    fileSizeString = if (fileS < 1024) {
        df.format(fileS.toDouble()).toString() + "B"
    } else if (fileS < 1048576) {
        df.format(fileS.toDouble() / 1024).toString() + "KB"
    } else if (fileS < 1073741824) {
        df.format(fileS.toDouble() / 1048576).toString() + "MB"
    } else {
        df.format(fileS.toDouble() / 1073741824).toString() + "GB"
    }
    return fileSizeString
}

/**
 * 转换文件大小,指定转换的类型
 *
 * @param fileS
 * @param sizeType
 * @return
 */
private fun FormatFileSize(fileS: Long, sizeType: Int): Double {
    val df = DecimalFormat("#.00")
    var fileSizeLong = 0.0
    when (sizeType) {
        SIZETYPE_B -> fileSizeLong = java.lang.Double.valueOf(df.format(fileS.toDouble()))
        SIZETYPE_KB -> fileSizeLong =
            java.lang.Double.valueOf(df.format(fileS.toDouble() / 1024))

        SIZETYPE_MB -> fileSizeLong =
            java.lang.Double.valueOf(df.format(fileS.toDouble() / 1048576))

        SIZETYPE_GB -> fileSizeLong =
            java.lang.Double.valueOf(df.format(fileS.toDouble() / 1073741824))

        else -> {
        }
    }
    return fileSizeLong
}