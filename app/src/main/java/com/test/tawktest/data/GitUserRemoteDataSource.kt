package com.test.tawktest.data

import com.test.tawktest.model.GitUser
import com.test.tawktest.model.GitUserDataSource
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.schedulers.Schedulers
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GitUserRemoteDataSource(apiClient: ApiClient) : GitUserDataSource {

    private lateinit var fetchUsersCall: Observable<List<GitUser>>
    private lateinit var fetchUserCall: Observable<GitUser>
    private lateinit var fetchFollowers: Observable<List<GitUser>>
    private lateinit var fetchFollowings: Observable<List<GitUser>>

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
        fetchFollowers = service.followers(login)
        fetchFollowings = service.followers(login)

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

    override fun cancel() {
//        fetchUsersCall?.let {
//            it.cancel()
//        }
    }
}