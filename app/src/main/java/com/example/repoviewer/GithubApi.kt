package com.example.repoviewer

import retrofit2.http.GET
import retrofit2.http.Header

interface GithubApi {
    @GET("user")
    suspend fun getUser(@Header("Authorization") token: String): UserInfo
}