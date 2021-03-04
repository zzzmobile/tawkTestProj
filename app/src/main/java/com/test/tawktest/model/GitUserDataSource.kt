package com.test.tawktest.model

import com.test.tawktest.data.FetchUserCallback
import com.test.tawktest.data.FetchUsersCallback

interface GitUserDataSource {
    fun retrieveUsers(page: Int, callback: FetchUsersCallback<GitUser>)
    fun retrieveUser(login: String, callback: FetchUserCallback<GitUser>)
    fun cancel()
}