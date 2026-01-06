package com.example.footballquiz

import android.content.Context
import android.telephony.TelephonyManager

object DeviceInfo {
    fun simCountryGeo(context: Context): String? {
        val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as? TelephonyManager
        return telephonyManager?.simCountryIso?.takeIf { it.isNotBlank() }?.uppercase()
    }
}

