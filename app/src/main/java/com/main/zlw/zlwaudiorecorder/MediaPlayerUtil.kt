package com.main.zlw.zlwaudiorecorder

import android.media.MediaPlayer

/**
 * DATE : 2022/7/4
 * @author : 景天
 */
object MediaPlayerUtil {

    private var mediaPlayer: MediaPlayer? = null
    private var currentMediaUrl = ""

    fun play(url: String, callback: OnCallback) {
        mediaPlayer?.apply {
            stop()
            release()
            mediaPlayer = null
            callback.onStop()
            //如果是相同的，就停止播放
            if (url == currentMediaUrl) {
                return
            }
        }
        currentMediaUrl = url
        callback.onPrepare()
        mediaPlayer = MediaPlayer()
        try {
            mediaPlayer!!.setDataSource(url)
            mediaPlayer!!.setOnPreparedListener {
                mediaPlayer?.start()
                callback.onStar()
            }
            mediaPlayer!!.setOnCompletionListener {
                mediaPlayer?.release()
                mediaPlayer = null
                callback.onCompletion()
            }
            mediaPlayer!!.setOnErrorListener { mediaPlayer, i, i2 ->
                callback.onError("播放错误：${i},${i2}")
                false
            }
            mediaPlayer!!.prepareAsync()
        } catch (e: Exception) {
            e.message?.let { callback.onError(it) }
        }
    }

    fun stop() {
        mediaPlayer?.stop()
    }

    fun release() {
        mediaPlayer?.release()
        mediaPlayer = null
    }

    interface OnCallback {
        fun onPrepare()
        fun onStar()
        fun onStop()
        fun onCompletion()
        fun onError(msg: String)
    }
}