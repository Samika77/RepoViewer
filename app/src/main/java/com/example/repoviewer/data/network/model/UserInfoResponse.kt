package com.example.repoviewer.data.network.model

import kotlinx.serialization.Serializable
import com.example.repoviewer.domain.model.UserInfo

@Serializable
data class UserInfoResponse(
    val tokenValid: Boolean = true
)

fun UserInfoResponse.toDomain(): UserInfo {
    return UserInfo(isTokenValid = this.tokenValid)
}