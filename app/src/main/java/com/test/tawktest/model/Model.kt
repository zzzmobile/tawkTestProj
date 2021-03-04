package com.test.tawktest.model

import java.io.Serializable

data class GitUser(
    val id: Int,
    val login: String,
    val avatar_url: String,
    val url: String
    ) : Serializable