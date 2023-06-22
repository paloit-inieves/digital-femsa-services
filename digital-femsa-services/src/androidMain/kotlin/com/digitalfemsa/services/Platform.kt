package com.digitalfemsa.services

import android.content.Context

class AndroidPlatform(override val platformContext: Context) : Platform

actual fun getPlatform(platformContext: Any): Platform = AndroidPlatform(platformContext as Context)
