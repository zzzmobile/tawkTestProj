package com.test.tawktest.data

interface FetchUsersCallback<T> {
    fun onSuccess(data: List<T>?)
    fun onError(error: String?)
}