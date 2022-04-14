package com.kiosk.example.db.encryption

import android.os.Build
import android.os.Build.VERSION_CODES
import androidx.annotation.RequiresApi
import java.security.SecureRandom

@RequiresApi(VERSION_CODES.O)
fun RawKeyGenerator(): ByteArray = ByteArray(32).apply {
    if (Build.VERSION.SDK_INT >= VERSION_CODES.O) {
        SecureRandom.getInstanceStrong().nextBytes(this)
    } else {
        SecureRandom().nextBytes(this)
    }
}