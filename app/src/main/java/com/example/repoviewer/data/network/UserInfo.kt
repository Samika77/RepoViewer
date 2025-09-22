package com.example.repoviewer.data.network

import kotlinx.serialization.Serializable
import com.example.repoviewer.domain.model.UserInfo

@Serializable
data class UserInfo(
    val tokenValid: Boolean = true
)

fun com.example.repoviewer.data.network.UserInfo.toDomain(): UserInfo {
    return UserInfo(isTokenValid = this.tokenValid)
}