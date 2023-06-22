package com.digitalfemsa.services

import platform.UIKit.UIApplication

class IOSPlatform(override val platformContext: UIApplication) : Platform

actual fun getPlatform(platformContext: Any): Platform =
    IOSPlatform(platformContext as UIApplication)
