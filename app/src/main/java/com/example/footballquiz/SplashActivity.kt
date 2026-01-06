package com.example.footballquiz

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import com.example.footballquiz.ui.theme.FootballQuizTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SplashActivity : ComponentActivity() {
    private var urlToOpen: String? = null
    private var customTabsOpened = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FootballQuizTheme {
                SplashScreen()
                LaunchedEffect(Unit) {
                    val bundle = packageName
                    val existingUrl = AppPrefs.getUrl(this@SplashActivity, bundle)
                    if (existingUrl == null) {
                        val url = withContext(Dispatchers.IO) {
                            ServerApi.fetchUrl(this@SplashActivity)
                        }
                        url?.let {
                            AppPrefs.saveUrl(this@SplashActivity, bundle, it)
                            urlToOpen = it
                        }
                    } else {
                        urlToOpen = existingUrl
                    }
                    urlToOpen?.let {
                        customTabsOpened = true
                        CustomTabsHelper.openUrl(this@SplashActivity, it)
                    } ?: run {
                        delay(2500)
                        startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                        finish()
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (customTabsOpened && urlToOpen != null) {
            CustomTabsHelper.openUrl(this, urlToOpen!!)
        }
    }
}

