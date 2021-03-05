package com.test.tawktest.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.test.tawktest.data.FetchUserCallback
import com.test.tawktest.model.GitUser
import com.test.tawktest.model.GitUserRepository

class GitUserViewModel(private val listRepository: GitUserRepository) : ViewModel() {

    private val _user = MutableLiveData<GitUser>().apply { value = null }
    var user: LiveData<GitUser> = _user

    private val _isViewLoading = MutableLiveData<Boolean>()
    val isViewLoading: LiveData<Boolean> = _isViewLoading

    private val _onMessageError = MutableLiveData<Any>()
    val onMessageError: LiveData<Any> = _onMessageError

    fun loadGitUser(login: String) {
        _isViewLoading.value = true
        listRepository.fetchUser(login, object : FetchUserCallback<GitUser> {
            override fun onSuccess(data: GitUser) {
                _isViewLoading.postValue(false)
                _user.postValue(data)
            }

            override fun onError(error: String?) {
                _isViewLoading.postValue(false)
                _onMessageError.postValue(error)
            }
        })
    }
}