package com.example.repoviewer.data.repository

import com.example.repoviewer.data.network.GithubApi
import com.example.repoviewer.data.storage.KeyValueStorage
import com.example.repoviewer.data.network.model.toDomain
import com.example.repoviewer.domain.model.Readme
import com.example.repoviewer.domain.model.RepoDetails
import com.example.repoviewer.domain.model.UserInfo
import com.example.repoviewer.domain.model.Repo
import javax.inject.Inject

class AppRepository @Inject constructor(
    private val api: GithubApi,
    private val storage: KeyValueStorage
) {
    suspend fun signIn(token: String): UserInfo {
        storage.authToken = token
        val userInfo = api.getUser()
        return userInfo.copy(tokenValid = true).toDomain()
    }

    suspend fun getRepositories(): List<Repo> {
        val networkRepos = api.getUserRepositories(perPage = 10)
        return networkRepos.map { it.toDomain() }
    }

    suspend fun getRepository(repoId: String): RepoDetails {
        val (owner, repoName) = repoId.split("/")
        val repoDetailsResponse = api.getRepository(
            owner = owner,
            repoName = repoName
        )
        return repoDetailsResponse.toDomain()
    }

    suspend fun getRepositoryReadme(
        ownerName: String,
        repositoryName: String
    ): Readme {
        val readmeResponse = api.getRepositoryReadme(
            owner = ownerName,
            repoName = repositoryName
        )
        return readmeResponse.toDomain()
    }
}