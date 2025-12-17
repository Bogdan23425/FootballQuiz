package com.example.footballquiz

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool

enum class MusicMode { MENU, QUIZ }

object AudioManager {
    private var music: MediaPlayer? = null
    private var currentMode: MusicMode? = null

    private var soundPool: SoundPool? = null
    private var sfxCorrectId: Int = 0
    private var sfxWrongId: Int = 0
    private var sfxLifeLostId: Int = 0
    private var sfxTimeWarningId: Int = 0
    private var sfxGameOverId: Int = 0
    private var sfxWinId: Int = 0

    fun init(context: Context) {
        if (soundPool == null) {
            val attrs = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()

            soundPool = SoundPool.Builder()
                .setMaxStreams(6)
                .setAudioAttributes(attrs)
                .build()

            soundPool!!.apply {
                sfxCorrectId = load(context, R.raw.sfx_correct, 1)
                sfxWrongId = load(context, R.raw.sfx_wrong, 1)
                sfxLifeLostId = load(context, R.raw.sfx_life_lost, 1)
                sfxTimeWarningId = load(context, R.raw.sfx_time_warning, 1)
                sfxGameOverId = load(context, R.raw.sfx_game_over, 1)
                sfxWinId = load(context, R.raw.sfx_win, 1)
            }
        }
    }

    fun setMusicMode(context: Context, mode: MusicMode) {
        if (!AppPrefs.isMusicEnabled(context)) {
            stopMusic()
            currentMode = mode
            return
        }

        if (currentMode == mode && music?.isPlaying == true) return

        currentMode = mode
        val res = when (mode) {
            MusicMode.MENU -> R.raw.music_menu
            MusicMode.QUIZ -> R.raw.music_quiz
        }

        music?.release()
        music = MediaPlayer.create(context, res).apply {
            isLooping = true
            val v = AppPrefs.musicVolume(context)
            setVolume(v, v)
            start()
        }
    }

    fun refreshVolumes(context: Context) {
        val mv = AppPrefs.musicVolume(context)
        val sv = AppPrefs.sfxVolume(context)
        music?.setVolume(mv, mv)
        cachedSfxVolume = sv
    }

    fun stopMusic() {
        music?.pause()
    }

    fun release() {
        music?.release()
        music = null
        currentMode = null

        soundPool?.release()
        soundPool = null

        sfxCorrectId = 0
        sfxWrongId = 0
        sfxLifeLostId = 0
        sfxTimeWarningId = 0
        sfxGameOverId = 0
        sfxWinId = 0
    }

    private var cachedSfxVolume: Float = 1f

    private fun play(context: Context, id: Int) {
        if (!AppPrefs.isSfxEnabled(context)) return
        val v = cachedSfxVolume.takeIf { it in 0f..1f } ?: AppPrefs.sfxVolume(context)
        soundPool?.play(id, v, v, 1, 0, 1f)
    }

    fun playCorrect(context: Context) = play(context, sfxCorrectId)
    fun playWrong(context: Context) = play(context, sfxWrongId)
    fun playLifeLost(context: Context) = play(context, sfxLifeLostId)
    fun playTimeWarning(context: Context) = play(context, sfxTimeWarningId)
    fun playGameOver(context: Context) = play(context, sfxGameOverId)
    fun playWin(context: Context) = play(context, sfxWinId)
}
