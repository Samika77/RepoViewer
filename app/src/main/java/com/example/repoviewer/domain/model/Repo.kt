package com.example.repoviewer.domain.model

data class Repo(
    val id: Long,
    val name: String,
    val language: String?,
    val description: String?,
    val ownerLogin: String
) {
    val repoId: String
        get() = "$ownerLogin/$name"
}
