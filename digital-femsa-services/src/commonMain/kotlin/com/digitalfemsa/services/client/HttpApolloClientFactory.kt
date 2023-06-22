package com.digitalfemsa.services.client

import com.apollographql.apollo3.ApolloClient
import com.digitalfemsa.services.client.middleware.ApolloConfig
import com.digitalfemsa.services.client.middleware.HttpMiddleware

class HttpApolloClientFactory() {
    private var middlewares: MutableList<HttpMiddleware> = mutableListOf()

    constructor(middlewares: List<HttpMiddleware>) : this() {
        this.middlewares.addAll(middlewares)
    }

    fun addMiddleware(middleware: HttpMiddleware) {
        this.middlewares.add(middleware)
    }

    fun create(apolloConfig: ApolloConfig): ApolloClient {
        var config: ApolloConfig = apolloConfig
        middlewares.forEach {
            config = it.apply(config)
        }
        return config.builder.build()
    }
}
