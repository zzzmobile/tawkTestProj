package com.test.tawktest.data

import com.test.tawktest.model.GitUser
import com.test.tawktest.model.GitUserDataSource
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers

class GitUserRemoteDataSource(apiClient: ApiClient) : GitUserDataSource {

    private lateinit var fetchUsersCall: Observable<List<GitUser>>
    private lateinit var fetchUserCall: Observable<GitUser>

    private val service = apiClient.build()

    override fun retrieveUsers(page: Int, callback: FetchUsersCallback<GitUser>) {
        fetchUsersCall = service!!.users(page)
        fetchUsersCall.subscribe(
            { value ->  callback.onSuccess(value) },
            { error -> callback.onError(error.message) }
        )
    }

    override fun retrieveUser(login: String, callback: FetchUserCallback<GitUser>) {
        fetchUserCall = service!!.user(login)

        fetchUserCall.subscribeOn(Schedulers.io())
        .subscribe(
            { response ->
                callback.onSuccess(response)
            },
            { error ->
                callback.onError(error.message)
            }
        )
    }
}