package com.test.tawktest.model

import java.io.Serializable

data class GitUser(
    val id: Int,
    val login: String,
    val name: String,
    val avatar_url: String,
    val url: String,
    val company: String,
    val blog: String,
    val email: String,
    val location: String,
    val bio: String,
    var followers : Int,
    var following : Int
    ) : Serializable