package com.example.repoviewer.data.network

import com.example.repoviewer.domain.model.Readme
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

@Serializable
data class ReadmeResponse(
    val content: String,
    val encoding: String
)

fun RepoDetails.toDomain(): com.example.repoviewer.domain.model.RepoDetails {
    return com.example.repoviewer.domain.model.RepoDetails(
        id = this.id,
        name = this.name,
        htmlUrl = this.htmlUrl,
        licenseName = this.license?.name ?: "",
        stargazersCount = this.stargazersCount,
        forksCount = this.forksCount,
        watchersCount = this.watchersCount,
        defaultBranch = this.defaultBranch,
        ownerLogin = this.owner.login
    )
}

fun ReadmeResponse.toDomain(): Readme {
    val decodedBytes = android.util.Base64.decode(this.content, android.util.Base64.DEFAULT)
    val decodedContent = String(decodedBytes, Charsets.UTF_8)
    return Readme(content = decodedContent)
}