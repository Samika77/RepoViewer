package com.example.repoviewer.data.network

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GithubApi {
    @GET("user")
    suspend fun getUser(): UserInfo

    @GET("user/repos")
    suspend fun getUserRepositories(
        @Query("per_page") perPage: Int = 10
    ): List<Repo>

    @GET("repos/{owner}/{repo}")
    suspend fun getRepository(
        @Path("owner") owner: String,
        @Path("repo") repoName: String
    ): RepoDetails

    @GET("repos/{owner}/{repo}/readme")
    suspend fun getRepositoryReadme(
        @Path("owner") owner: String,
        @Path("repo") repoName: String
    ): ReadmeResponse
}