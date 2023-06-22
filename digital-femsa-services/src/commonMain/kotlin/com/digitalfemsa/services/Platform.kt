package com.digitalfemsa.services

interface Platform {
    val platformContext: Any
}

expect fun getPlatform(platformContext: Any): Platform
