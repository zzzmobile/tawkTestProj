package com.test.tawktest.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.test.tawktest.data.FetchUserCallback
import com.test.tawktest.model.GitUser
import com.test.tawktest.model.GitUserRepository
import com.test.tawktest.model.NoteRepository
import com.test.tawktest.model.UserNoteTableModel

class GitUserViewModel(private val listRepository: GitUserRepository) : ViewModel() {

    private val _user = MutableLiveData<GitUser>().apply { value = null }
    var user: LiveData<GitUser> = _user

    var liveDataNote: LiveData<UserNoteTableModel>? = null

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

    fun insertData(context: Context, login: String, note: String) {
        NoteRepository.insertData(context, login, note)
    }

    fun getUserNote(context: Context, login: String) : LiveData<UserNoteTableModel>? {
        liveDataNote = NoteRepository.getUserNote(context, login)
        return liveDataNote
    }
}