package com.digitalfemsa.services

import com.apollographql.apollo3.ApolloClient
import com.digitalfemsa.services.client.HttpApolloClientFactory
import com.digitalfemsa.services.client.middleware.ApolloConfig
import com.digitalfemsa.services.client.middleware.ImpervaMiddleware
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.internal.SynchronizedObject
import kotlinx.coroutines.internal.synchronized
import kotlin.jvm.Volatile

object Apollo {
    @Volatile
    private var client: ApolloClient? = null

    @OptIn(InternalCoroutinesApi::class)
    private val lock = SynchronizedObject()

    @OptIn(InternalCoroutinesApi::class)
    fun build(
        url: String,
        withFactory: HttpApolloClientFactory?,
        platformContext: Platform,
        applyProtection: Boolean = true,
    ): ApolloClient {
        if (client == null) {
            synchronized(lock) {
                val factory = withFactory ?: HttpApolloClientFactory()
                if (applyProtection) {
                    factory.addMiddleware(ImpervaMiddleware(platform = getPlatform(platformContext)))
                }
                val apolloBuilder = ApolloClient.Builder()
                    .serverUrl(serverUrl = url)
                return factory.create(ApolloConfig(builder = apolloBuilder, url))
            }
        }
        return client!!
    }

    fun getApi(): ApolloApi {
        client?.let {
            return ApolloApiImpl(it)
        }
        throw IllegalStateException("Client not initialized")
    }
}
