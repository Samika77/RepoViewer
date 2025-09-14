package com.example.repoviewer.data.repository

import com.example.repoviewer.data.network.GithubApi
import com.example.repoviewer.data.network.Repo
import com.example.repoviewer.data.storage.KeyValueStorage
import com.example.repoviewer.data.network.UserInfo
import javax.inject.Inject

class AppRepository @Inject constructor(
    private val api: GithubApi,
    private val storage: KeyValueStorage
) {
    suspend fun signIn(token: String): UserInfo {
        val authToken = "token $token"
        val userInfo = api.getUser(authToken)
        if (userInfo.tokenValid) {
            storage.authToken = token
        }
        return userInfo
    }

    suspend fun getRepositories(): List<Repo> {
        val token = storage.authToken!!
        val authHeader = "token $token"
        return api.getUserRepositories(authHeader, perPage = 10)
    }
}