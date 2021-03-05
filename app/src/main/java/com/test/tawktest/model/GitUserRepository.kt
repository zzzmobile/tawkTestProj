package com.test.tawktest.model

import com.test.tawktest.data.FetchUserCallback
import com.test.tawktest.data.FetchUsersCallback

class GitUserRepository(private val gitUserDataSource: GitUserDataSource) {
    fun fetchUsers(page: Int, callback: FetchUsersCallback<GitUser>) {
        gitUserDataSource.retrieveUsers(page, callback)
    }

    fun cancel() {
        gitUserDataSource.cancel()
    }

    fun fetchUser(login: String, callback: FetchUserCallback<GitUser>) {
        gitUserDataSource.retrieveUser(login, callback)
    }
}