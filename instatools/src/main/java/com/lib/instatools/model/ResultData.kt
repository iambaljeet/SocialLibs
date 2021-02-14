package com.lib.instatools.model

sealed class ResultData<T> {
    data class Success<T>(val data: T): ResultData<T>()
    data class Failure<T>(val message: String? = null, val throwable: Throwable? = null): ResultData<T>()
}