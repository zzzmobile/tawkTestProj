package com.test.tawktest.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.test.tawktest.data.FetchUsersCallback
import com.test.tawktest.model.GitUser
import com.test.tawktest.model.GitUserRepository

class GitUserListViewModel(private val listRepository: GitUserRepository) : ViewModel() {

    private val _users = MutableLiveData<List<GitUser>>().apply { value = emptyList() }
    val users: LiveData<List<GitUser>> = _users

    private val _isViewLoading = MutableLiveData<Boolean>()
    val isViewLoading: LiveData<Boolean> = _isViewLoading

    private val _onMessageError = MutableLiveData<Any>()
    val onMessageError: LiveData<Any> = _onMessageError

    private val _isEmptyList = MutableLiveData<Boolean>()
    val isEmptyList: LiveData<Boolean> = _isEmptyList

    fun loadGitUsers(page: Int) {
        _isViewLoading.value = true
        listRepository.fetchUsers(page, object : FetchUsersCallback<GitUser> {
            override fun onError(error: String?) {
                _isViewLoading.postValue(false)
                _onMessageError.postValue(error)
            }

            override fun onSuccess(data: List<GitUser>?) {
                _isViewLoading.postValue(false)
                if (data.isNullOrEmpty()) {
                    _isEmptyList.postValue(true)
                } else {
                    _users.postValue(_users.value?.plus(data))
                }
            }
        })
    }

    fun clearUsers() {
        listRepository.cancel()
    }

}