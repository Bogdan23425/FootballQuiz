package com.example.footballquiz

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.footballquiz.ui.theme.FootballQuizTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AudioManager.init(this)
        AudioManager.refreshVolumes(this)

        enableEdgeToEdge()
        setContent {
            FootballQuizTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = androidx.compose.material3.MaterialTheme.colorScheme.background
                ) {
                    AppNav()
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        AudioManager.refreshVolumes(this)
    }

    override fun onStop() {
        super.onStop()
        AudioManager.stopMusic()
    }

    override fun onDestroy() {
        super.onDestroy()
        AudioManager.release()
    }
}
