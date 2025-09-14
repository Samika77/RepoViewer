package com.example.repoviewer.data.network

import kotlinx.serialization.Serializable

@Serializable
data class Repo(
    val id: Long,
    val name: String,
    val language: String,
    val description: String? = null,
    val owner: Owner
) {
    val repoId: String
        get() = "${owner.login}/$name"
}