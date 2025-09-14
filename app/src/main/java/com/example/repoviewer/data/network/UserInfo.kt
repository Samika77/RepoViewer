package com.example.repoviewer.data.network

import kotlinx.serialization.Serializable

@Serializable
data class UserInfo(
    val tokenValid: Boolean = true
)