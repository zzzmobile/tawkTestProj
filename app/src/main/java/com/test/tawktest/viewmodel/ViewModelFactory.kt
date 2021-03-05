package com.test.tawktest.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.test.tawktest.model.GitUserRepository

class ViewModelFactory(private val listRepository: GitUserRepository) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return if (modelClass == GitUserListViewModel::class.java)
            GitUserListViewModel(listRepository) as T
        else
            GitUserViewModel(listRepository) as T
    }
}