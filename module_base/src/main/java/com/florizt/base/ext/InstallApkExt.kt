@file:JvmName("InstallApkExt")

package com.florizt.base.ext

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.content.FileProvider
import java.io.File

fun Context.doInstallApk(file: File) {
    safe {
        startActivity(Intent().apply {
            action = "android.intent.action.VIEW"
            addCategory("android.intent.category.DEFAULT")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                val contentUri: Uri = FileProvider.getUriForFile(
                    this@doInstallApk,
                    packageName + ".fileprovider",
                    file
                )
                setDataAndType(contentUri, "application/vnd.android.package-archive")
            } else {
                setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive")
            }
        })
    }
}

fun Context.startAppSettings() {
    safe {
        startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            data = Uri.parse("package:" + packageName)
        })
    }
}