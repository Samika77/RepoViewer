package com.example.repoviewer

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RepoDetails(
    val id: Long,
    val name: String,

    @SerialName("html_url")
    val htmlUrl: String,

    val license: LicenseInfo? = null,

    @SerialName("stargazers_count")
    val stargazersCount: Int,

    @SerialName("forks_count")
    val forksCount: Int,

    @SerialName("watchers_count")
    val watchersCount: Int,

    @SerialName("default_branch")
    val defaultBranch: String,

    val owner: Owner
) {
    val repoId: String
        get() = "${owner.login}/$name"
}

@Serializable
data class LicenseInfo(
    val name: String? = null
)

@Serializable
data class Owner(
    val login: String
)