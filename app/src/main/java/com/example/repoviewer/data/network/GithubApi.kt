package com.example.repoviewer.data.network

import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface GithubApi {
    @GET("user")
    suspend fun getUser(@Header("Authorization") token: String): UserInfo

    @GET("user/repos")
    suspend fun getUserRepositories(
        @Header("Authorization") token: String,
        @Query("per_page") perPage: Int = 10
    ): List<Repo>
}