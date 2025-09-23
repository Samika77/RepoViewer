package com.example.repoviewer.data.repository

import com.example.repoviewer.data.network.GithubApi
import com.example.repoviewer.data.network.toDomain
import com.example.repoviewer.data.storage.KeyValueStorage
import com.example.repoviewer.domain.model.UserInfo
import com.example.repoviewer.domain.model.Repo
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
        return userInfo.toDomain()
    }

    suspend fun getRepositories(): List<Repo> {
        val token = storage.authToken!!
        val authHeader = "token $token"
        val networkRepos = api.getUserRepositories(authHeader, perPage = 10)
        return networkRepos.map { it.toDomain() }
    }
}