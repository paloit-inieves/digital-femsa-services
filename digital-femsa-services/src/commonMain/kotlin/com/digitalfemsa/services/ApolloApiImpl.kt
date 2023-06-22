package com.digitalfemsa.services

import com.apollographql.apollo3.ApolloClient

class ApolloApiImpl(val client: ApolloClient) : ApolloApi {
    override suspend fun loginSdk() {
    }
}
