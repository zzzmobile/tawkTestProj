package com.test.tawktest.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.test.tawktest.data.FetchUsersCallback
import com.test.tawktest.model.GitUser
import com.test.tawktest.model.GitUserRepository
import com.test.tawktest.model.NoteRepository
import com.test.tawktest.model.UserNoteTableModel

/**
 * userlist viewmodel
 */
class GitUserListViewModel(private val listRepository: GitUserRepository) : ViewModel() {

    private val _users = MutableLiveData<List<GitUser>>().apply { value = emptyList() }
    val users: LiveData<List<GitUser>> = _users

    private val _searchUsers = MutableLiveData<List<GitUser>>().apply { value = emptyList() }
    val searchUsers: LiveData<List<GitUser>> = _searchUsers

    var liveDataNote: LiveData<List<UserNoteTableModel>>? = null

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

    // clear users
    fun clearUsers() {
        _users.value = emptyList()
    }

    // search users with login string
    fun searchUserWithName(name: String) {
        val searchUserList = ArrayList<GitUser>()
        for (user in users.value!!.iterator())
            if (user.login.toLowerCase().contains(name.toLowerCase()))
                searchUserList.add(user)
        _searchUsers.postValue(searchUserList)
    }

    // get all users's note from Room database
    fun getAllUserNotes(context: Context) : LiveData<List<UserNoteTableModel>>? {
        liveDataNote = NoteRepository.getAllUserNotes(context)
        return liveDataNote
    }
}