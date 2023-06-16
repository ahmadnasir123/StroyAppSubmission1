package com.sirdev.storyapp.data.remote.response.auth

data class AuthSession(
    var name: String,
    var token: String,
    var userId: String,
    var isLogin: Boolean
)