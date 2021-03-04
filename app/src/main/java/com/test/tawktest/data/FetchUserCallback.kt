package com.test.tawktest.data

interface FetchUserCallback<T> {
    fun onSuccess(data: T)
    fun onError(error: String?)
}