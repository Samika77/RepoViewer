package com.example.repoviewer.data.network

import kotlinx.serialization.Serializable
import com.example.repoviewer.domain.model.Repo

@Serializable
data class Repo(
    val id: Long,
    val name: String,
    val language: String? = null,
    val description: String? = null,
    val owner: Owner
) {
    val repoId: String
        get() = "${owner.login}/$name"
}

fun com.example.repoviewer.data.network.Repo.toDomain(): Repo {
    return Repo(
        id = this.id,
        name = this.name,
        language = this.language,
        description = this.description,
        ownerLogin = this.owner.login
    )
}