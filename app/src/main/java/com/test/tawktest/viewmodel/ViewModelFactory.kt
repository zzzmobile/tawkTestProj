package com.test.tawktest.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.test.tawktest.model.GitUserRepository

class ViewModelFactory(private val repository: GitUserRepository) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return GitUserViewModel(repository) as T
    }
}