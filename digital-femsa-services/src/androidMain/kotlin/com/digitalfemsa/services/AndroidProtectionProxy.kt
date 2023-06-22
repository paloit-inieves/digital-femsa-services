package com.digitalfemsa.services

import android.content.Context
import com.distil.protection.android.Protection
import java.net.URL

class AndroidProtectionProxy(private val context: Context) : ProtectionProxy {
    private var protection: Protection? = null

    @Throws(Exception::class)
    override fun blockingGetToken(): String = protection?.blockingGetToken() ?: ""

    override fun protection(url: String) {
        protection = Protection.protection(context, URL(url))
    }

    companion object {
        @Volatile
        private var protectionProxy: AndroidProtectionProxy? = null

        fun getInstance(context: Context? = null): AndroidProtectionProxy {
            if (protectionProxy == null) {
                synchronized(AndroidProtectionProxy::class) {
                    if (context != null) {
                        if (protectionProxy == null) {
                            protectionProxy = AndroidProtectionProxy(context = context)
                        }
                    } else {
                        throw IllegalStateException("missing android context")
                    }
                }
            }
            return protectionProxy!!
        }
    }
}

actual fun getProtection(platform: Platform): ProtectionProxy =
    AndroidProtectionProxy.getInstance(platform.platformContext as Context)
