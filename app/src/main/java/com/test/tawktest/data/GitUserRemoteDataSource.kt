package com.test.tawktest.data

import com.test.tawktest.model.GitUser
import com.test.tawktest.model.GitUserDataSource
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GitUserRemoteDataSource(apiClient: ApiClient) : GitUserDataSource {

    private var fetchUsersCall: Call<List<GitUser>>? = null
    private var fetchUserCall: Call<GitUser>? = null
    private val service = apiClient.build()

    override fun retrieveUsers(page: Int, callback: FetchUsersCallback<GitUser>) {
        fetchUsersCall = service?.users(page)
        fetchUsersCall?.enqueue(object : Callback<List<GitUser>> {
            override fun onFailure(call: Call<List<GitUser>>, t: Throwable) {
                callback.onError(t.message)
            }

            override fun onResponse(call: Call<List<GitUser>>, response: Response<List<GitUser>>) {
                response.body()?.let {
                    if (response.isSuccessful) {
                        callback.onSuccess(it)
                    } else {
                        callback.onError(response.message())
                    }
                }
            }
        })
    }

    override fun retrieveUser(login: String, callback: FetchUserCallback<GitUser>) {
//        fetchUserCall = service?.users()
//        fetchUserCall?.enqueue(object : Callback<GitUser> {
//            override fun onFailure(call: Call<GitUser>, t: Throwable) {
//                callback.onError(t.message)
//            }
//
//            override fun onResponse(call: Call<GitUser>, response: Response<GitUser>) {
//                response.body()?.let {
//                    if (response.isSuccessful) {
//                        callback.onSuccess(it)
//                    } else {
//                        callback.onError(response.message())
//                    }
//                }
//            }
//        })
    }

    override fun cancel() {
        fetchUsersCall?.let {
            it.cancel()
        }
    }
}