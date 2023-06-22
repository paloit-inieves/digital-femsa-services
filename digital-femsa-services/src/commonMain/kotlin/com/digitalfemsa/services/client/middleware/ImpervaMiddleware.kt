package com.digitalfemsa.services.client.middleware

import com.apollographql.apollo3.api.http.HttpRequest
import com.apollographql.apollo3.api.http.HttpResponse
import com.apollographql.apollo3.network.http.HttpInterceptor
import com.apollographql.apollo3.network.http.HttpInterceptorChain
import com.digitalfemsa.services.Platform
import com.digitalfemsa.services.getProtection

class ImpervaMiddleware(private val platform: Platform) : HttpMiddleware, HttpInterceptor {
    companion object {
        const val IMPERVA_TOKEN = "X-D-Token"
    }

    val protection = getProtection(platform)
    override fun apply(apolloConfig: ApolloConfig): ApolloConfig {
        protection.protection(apolloConfig.url)
        apolloConfig.builder.addHttpInterceptor(this)
        return ApolloConfig(builder = apolloConfig.builder, url = apolloConfig.url)
    }

    override suspend fun intercept(
        request: HttpRequest,
        chain: HttpInterceptorChain,
    ): HttpResponse {
        val token = blockingGetTokenOrNull()
        val newRequest = addTokenToRequest(request, token)
        return chain.proceed(newRequest)
    }

    private fun addTokenToRequest(request: HttpRequest, token: String?): HttpRequest {
        if (!token.isNullOrEmpty()) {
            return request.newBuilder()
                .addHeader(IMPERVA_TOKEN, token)
                .build()
        }
        return request
    }

    private fun blockingGetTokenOrNull(): String? {
        return try {
            protection.blockingGetToken()
        } catch (_: Exception) {
            null
        }
    }
}
