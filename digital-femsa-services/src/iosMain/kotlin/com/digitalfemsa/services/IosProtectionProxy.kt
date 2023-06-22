package com.digitalfemsa.services

import distil_protection.Protection
import platform.Foundation.NSURL

class IosProtectionProxy : ProtectionProxy {
    private var protection: Protection? = null

    override fun blockingGetToken(): String {
        return protection?.getToken(error = null) ?: ""
    }

    override fun protection(url: String) {
        val nsUrl = NSURL.URLWithString(url)
        protection = Protection(nsUrl!!)
    }
}

actual fun getProtection(platform: Platform): ProtectionProxy = IosProtectionProxy()
