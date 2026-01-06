package com.example.footballquiz

import android.app.Activity
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent

object CustomTabsHelper {
    fun openUrl(activity: Activity, url: String) {
        val uri = Uri.parse(url)
        val customTabsIntent = CustomTabsIntent.Builder().build()
        customTabsIntent.launchUrl(activity, uri)
    }
}

