package com.example.footballquiz

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.footballquiz.ui.theme.FootballQuizTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AudioManager.init(this)

        enableEdgeToEdge()
        setContent {
            FootballQuizTheme {
                AppNav()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        AudioManager.startMusic(this)
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
