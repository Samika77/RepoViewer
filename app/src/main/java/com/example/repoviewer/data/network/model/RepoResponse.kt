package com.example.repoviewer.data.network.model

import kotlinx.serialization.Serializable
import com.example.repoviewer.domain.model.Repo
import kotlinx.serialization.SerialName

@Serializable
data class RepoResponse(
    @SerialName("id")
    val id: Long,

    @SerialName("name")
    val name: String,

    @SerialName("language")
    val language: String? = null,

    @SerialName("description")
    val description: String? = null,

    @SerialName("owner")
    val owner: Owner
) {
    val repoId: String
        get() = "${owner.login}/$name"
}

fun RepoResponse.toDomain(): Repo {
    return Repo(
        id = this.id,
        name = this.name,
        language = this.language,
        description = this.description,
        ownerLogin = this.owner.login
    )
}