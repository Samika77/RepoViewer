package com.example.repoviewer

import kotlinx.serialization.Serializable

@Serializable
data class Repo(
    val id: Long,
    val name: String,
    val language: String,
    val description: String? = null
)