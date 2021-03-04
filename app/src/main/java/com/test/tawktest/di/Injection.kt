package com.test.tawktest.di

import androidx.lifecycle.ViewModelProvider
import com.test.tawktest.data.ApiClient
import com.test.tawktest.data.GitUserRemoteDataSource
import com.test.tawktest.model.GitUserDataSource
import com.test.tawktest.model.GitUserRepository
import com.test.tawktest.viewmodel.ViewModelFactory

object Injection {
    private val gitUserDataSource: GitUserDataSource = GitUserRemoteDataSource(ApiClient)
    private val gitUserRepository = GitUserRepository(gitUserDataSource)
    private val gitUserViewModelFactory = ViewModelFactory(gitUserRepository)

    fun providerRepository(): GitUserDataSource {
        return gitUserDataSource
    }

    fun provideViewModelFactory(): ViewModelProvider.Factory {
        return gitUserViewModelFactory
    }
}