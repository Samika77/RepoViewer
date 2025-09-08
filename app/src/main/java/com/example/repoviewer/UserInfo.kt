package com.example.repoviewer

import kotlinx.serialization.Serializable

@Serializable
data class UserInfo(
    val tokenValid: Boolean = true
)
