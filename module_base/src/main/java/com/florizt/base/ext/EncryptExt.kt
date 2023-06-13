package com.florizt.base.ext

import android.annotation.SuppressLint
import android.util.Base64
import java.io.File
import java.io.FileInputStream
import java.math.BigInteger
import java.nio.channels.FileChannel
import java.security.MessageDigest
import java.util.Locale
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

const val DESede = "DESede"

/**
 * 3DES加密
 * @receiver String
 * @param psw String
 * @return String
 */
@SuppressLint("GetInstance")
fun String.encrypt3DES(psw: String): String { // 恢复密钥
    return String(
        Base64.encode(
            Cipher.getInstance(DESede).run {
                init(Cipher.ENCRYPT_MODE, SecretKeySpec(build3Deskey(psw.toByteArray()), DESede))
                doFinal(toByteArray(Charsets.UTF_8))
            },
            Base64.DEFAULT
        ),
        Charsets.UTF_8
    )
}

/**
 * 3DES解密
 * @param data 加密后的字符串
 * @return
 */
@SuppressLint("GetInstance")
fun String.decrypt3DES(psw: String): String { // 恢复密钥
    return String(
        Cipher.getInstance(DESede).run {
            init(Cipher.DECRYPT_MODE, SecretKeySpec(build3Deskey(psw.toByteArray()), DESede))
            Base64.decode(toByteArray(), Base64.DEFAULT).run {
                doFinal(this)
            }
        },
        Charsets.UTF_8
    )
}

private fun build3Deskey(temp: ByteArray): ByteArray {
    val key = ByteArray(24)
    if (key.size > temp.size) {
        System.arraycopy(temp, 0, key, 0, temp.size)
    } else {
        System.arraycopy(temp, 0, key, 0, key.size)
    }
    return key
}

/**
 * 获取文件的md5
 * @receiver File
 * @return String?
 */
fun File.toMD5(): String? {
    var fis: FileInputStream? = null
    return safe(
        block = {
            fis = FileInputStream(this)
            BigInteger(
                1,
                MessageDigest.getInstance("MD5").run {
                    update(fis!!.channel.map(FileChannel.MapMode.READ_ONLY, 0, length()))
                    digest()
                }
            ).toString(16).uppercase(Locale.ENGLISH)
        },
        error = { null },
        finally = {
            safe { fis?.close() }
        }
    )
}
