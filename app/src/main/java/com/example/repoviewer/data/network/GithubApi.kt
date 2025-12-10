package com.example.repoviewer.data.network

import com.example.repoviewer.data.network.model.ReadmeResponse
import com.example.repoviewer.data.network.model.RepoResponse
import com.example.repoviewer.data.network.model.RepoDetailsResponse
import com.example.repoviewer.data.network.model.UserInfoResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GithubApi {
    @GET("user")
    suspend fun getUser(): UserInfoResponse

    @GET("user/repos")
    suspend fun getUserRepositories(
        @Query("per_page") perPage: Int = 10
    ): List<RepoResponse>

    @GET("repos/{owner}/{repo}")
    suspend fun getRepository(
        @Path("owner") owner: String,
        @Path("repo") repoName: String
    ): RepoDetailsResponse

    @GET("repos/{owner}/{repo}/readme")
    suspend fun getRepositoryReadme(
        @Path("owner") owner: String,
        @Path("repo") repoName: String
    ): ReadmeResponse
}