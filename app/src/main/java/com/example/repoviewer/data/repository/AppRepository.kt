package com.example.repoviewer.data.repository

import com.example.repoviewer.data.network.GithubApi
import com.example.repoviewer.data.network.Repo
import com.example.repoviewer.data.network.RepoDetails
import com.example.repoviewer.data.storage.KeyValueStorage
import com.example.repoviewer.data.network.UserInfo
import javax.inject.Inject

class AppRepository @Inject constructor(
    private val api: GithubApi,
    private val storage: KeyValueStorage
) {
    private fun getAuthHeader(): String {
        val token = storage.authToken!!
        return "token $token"
    }

    suspend fun signIn(token: String): UserInfo {
        val authToken = "token $token"
        val userInfo = api.getUser(authToken)
        if (userInfo.tokenValid) {
            storage.authToken = token
        }
        return userInfo
    }

    suspend fun getRepositories(): List<Repo> {
        return api.getUserRepositories(getAuthHeader(), perPage = 10)
    }

    suspend fun getRepository(repoId: String): RepoDetails {
        val (owner, repoName) = repoId.split("/")
        return api.getRepository(getAuthHeader(), owner, repoName)
    }

    suspend fun getRepositoryReadme(
        ownerName: String,
        repositoryName: String,
        branchName: String
    ): String {
        val readmeResponse = api.getRepositoryReadme(getAuthHeader(), ownerName, repositoryName)
        val decodedBytes =
            android.util.Base64.decode(readmeResponse.content, android.util.Base64.DEFAULT)
        return String(decodedBytes, Charsets.UTF_8)
    }
}