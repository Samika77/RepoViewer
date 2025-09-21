package com.example.repoviewer.data.repository

import com.example.repoviewer.data.network.GithubApi
import com.example.repoviewer.data.network.Repo
import com.example.repoviewer.data.storage.KeyValueStorage
import com.example.repoviewer.data.network.UserInfo
import com.example.repoviewer.data.network.toDomain
import com.example.repoviewer.domain.model.Readme
import com.example.repoviewer.domain.model.RepoDetails
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
        val repoDetailsResponse = api.getRepository(getAuthHeader(), owner, repoName)
        return repoDetailsResponse.toDomain()
    }

    suspend fun getRepositoryReadme(
        ownerName: String,
        repositoryName: String,
        branchName: String
    ): Readme {
        val readmeResponse = api.getRepositoryReadme(getAuthHeader(), ownerName, repositoryName)
        return readmeResponse.toDomain()
    }
}