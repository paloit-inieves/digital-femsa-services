package com.digitalfemsa.services.client.middleware

import com.apollographql.apollo3.ApolloClient

interface HttpMiddleware {
    fun apply(apolloConfig: ApolloConfig): ApolloConfig
}

data class ApolloConfig(
    val builder: ApolloClient.Builder,
    val url: String,
)
