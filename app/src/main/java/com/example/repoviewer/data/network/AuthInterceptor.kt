package com.example.repoviewer.data.network

import com.example.repoviewer.data.storage.KeyValueStorage
import javax.inject.Inject
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor @Inject constructor(
    private val storage: KeyValueStorage
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = storage.authToken
        if (token == null) {
            return chain.proceed(chain.request())
        }
        val request = chain.request().newBuilder()
            .header("Authorization", "token $token")
            .build()
        return chain.proceed(request)
    }
}