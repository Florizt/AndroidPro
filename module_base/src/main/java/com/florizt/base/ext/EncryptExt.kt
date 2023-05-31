package com.florizt.base.ext

import android.util.Base64
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.math.BigInteger
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import java.security.MessageDigest
import java.util.Locale
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.DESKeySpec
import javax.crypto.spec.SecretKeySpec

/**
 * 加密
 *
 * @param data 加密数据
 * @return
 * @throws Exception
 */

const val DESede = "DESede"

/**
 * 3DES加密
 * @receiver String
 * @param psw String
 * @return String
 */
fun String.encrypt3DES(psw: String): String { // 恢复密钥
    val secretKey: SecretKey =
        SecretKeySpec(build3Deskey(psw.toByteArray(charset("UTF-8"))), DESede)
    // Cipher完成加密
    val cipher: Cipher = Cipher.getInstance(DESede)
    // cipher初始化
    cipher.init(Cipher.ENCRYPT_MODE, secretKey)
    val encrypt: ByteArray = cipher.doFinal(toByteArray(charset("UTF-8")))
    //转码
    return String(Base64.encode(encrypt, Base64.DEFAULT), charset("UTF-8"))
}

/**
 * 3DES解密
 * @param data 加密后的字符串
 * @return
 * @throws Exception
 */
fun String.decrypt3DES(psw: String): String { // 恢复密钥
    val secretKey: SecretKey =
        SecretKeySpec(build3Deskey(psw.toByteArray(charset("UTF-8"))), DESede)
    // Cipher完成解密
    val cipher: Cipher = Cipher.getInstance(DESede)
    // 初始化cipher
    cipher.init(Cipher.DECRYPT_MODE, secretKey)
    //转码
    val bytes: ByteArray = Base64.decode(toByteArray(charset("UTF-8")), Base64.DEFAULT)
    //解密
    val plain: ByteArray = cipher.doFinal(bytes)
    //解密结果转码
    return String(plain, charset("UTF-8"))
}

private fun build3Deskey(temp: ByteArray): ByteArray? {
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
    var value: String? = null
    var fis: FileInputStream? = null
    try {
        fis = FileInputStream(this)
        val byteBuffer: MappedByteBuffer =
            fis.channel.map(FileChannel.MapMode.READ_ONLY, 0, length())
        val md5: MessageDigest = MessageDigest.getInstance("MD5")
        md5.update(byteBuffer)
        val bi = BigInteger(1, md5.digest())
        value = bi.toString(16).toUpperCase(Locale.ENGLISH) //转为大写
    } catch (e: java.lang.Exception) {
        e.printStackTrace()
    } finally {
        if (null != fis) {
            try {
                fis.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
    return value
}
