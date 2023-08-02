@file:JvmName("GsonExt")

package com.florizt.base.ext

import com.google.gson.Gson

inline fun <reified T> String.fromJson(): T =
    Gson().fromJson(this, T::class.java)

fun <T> T.toJson(): String = Gson().toJson(this)