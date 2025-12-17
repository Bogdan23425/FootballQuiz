package com.example.footballquiz

import android.content.Context
import android.media.MediaPlayer
import android.media.SoundPool

object AudioManager {
    private var music: MediaPlayer? = null

    private var soundPool: SoundPool? = null
    private var sfxCorrectId: Int = 0
    private var sfxWrongId: Int = 0

    fun init(context: Context) {
        // Инициализируем SoundPool один раз
        if (soundPool == null) {
            soundPool = SoundPool.Builder().setMaxStreams(2).build()
            sfxCorrectId = soundPool!!.load(context, R.raw.sfx_correct, 1)
            sfxWrongId = soundPool!!.load(context, R.raw.sfx_wrong, 1)
        }
    }

    fun startMusic(context: Context) {
        if (!AppPrefs.isMusicEnabled(context)) return

        if (music == null) {
            music = MediaPlayer.create(context, R.raw.music_bg).apply {
                isLooping = true
                setVolume(0.6f, 0.6f)
            }
        }
        if (music?.isPlaying != true) music?.start()
    }

    fun stopMusic() {
        music?.pause()
    }

    fun release() {
        music?.release()
        music = null

        soundPool?.release()
        soundPool = null
        sfxCorrectId = 0
        sfxWrongId = 0
    }

    fun playCorrect(context: Context) {
        if (!AppPrefs.isSfxEnabled(context)) return
        soundPool?.play(sfxCorrectId, 1f, 1f, 1, 0, 1f)
    }

    fun playWrong(context: Context) {
        if (!AppPrefs.isSfxEnabled(context)) return
        soundPool?.play(sfxWrongId, 1f, 1f, 1, 0, 1f)
    }
}
