package com.example.repoviewer.domain.model

data class RepoDetails(
    val id: Long,
    val name: String,
    val htmlUrl: String,
    val licenseName: String,
    val stargazersCount: Int,
    val forksCount: Int,
    val watchersCount: Int,
    val defaultBranch: String,
    val ownerLogin: String
)
data class Readme(
    val content: String
)