package com.example.repoviewer.data.network.model

import android.util.Base64
import com.example.repoviewer.domain.model.Readme
import com.example.repoviewer.domain.model.RepoDetails
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RepoDetailsResponse(
    @SerialName("id")
    val id: Long,

    @SerialName("name")
    val name: String,

    @SerialName("html_url")
    val htmlUrl: String,

    @SerialName("license")
    val license: LicenseInfo? = null,

    @SerialName("stargazers_count")
    val stargazersCount: Int,

    @SerialName("forks_count")
    val forksCount: Int,

    @SerialName("watchers_count")
    val watchersCount: Int,

    @SerialName("default_branch")
    val defaultBranch: String,

    @SerialName("owner")
    val owner: Owner
) {
    val repoId: String
        get() = "${owner.login}/$name"
}

@Serializable
data class LicenseInfo(
    @SerialName("name")
    val name: String? = null
)

@Serializable
data class Owner(
    @SerialName("login")
    val login: String
)

@Serializable
data class ReadmeResponse(
    @SerialName("content")
    val content: String,

    @SerialName("encoding")
    val encoding: String
)

fun RepoDetailsResponse.toDomain(): RepoDetails {
    return RepoDetails(
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
    val decodedBytes = Base64.decode(this.content, Base64.DEFAULT)
    val decodedContent = String(decodedBytes, Charsets.UTF_8)
    return Readme(content = decodedContent)
}