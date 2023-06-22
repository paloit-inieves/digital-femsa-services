package com.digitalfemsa.services

interface ProtectionProxy {
    @Throws(Exception::class)
    fun blockingGetToken(): String

    fun protection(url: String)
}

expect fun getProtection(platform: Platform): ProtectionProxy
