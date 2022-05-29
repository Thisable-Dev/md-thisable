package com.devthisable.thisable.utils

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.util.Log
import com.devthisable.thisable.R
import java.io.IOException
import java.lang.IllegalStateException

class SoundPlayer(private val context : Context ) {

    init {
        init()
    }
    private var isReady : Boolean = false
    private fun getResources(label : String )  : Int? {

        return when (label) {
            context.resources.getString(R.string.raw_sound_1) -> {
                R.raw.ipod_1
            }
            context.resources.getString(R.string.raw_sound_2) -> {
                R.raw.monitor_1
            }
            context.resources.getString(R.string.raw_sound_3) -> {
                R.raw.turu_1
            }
            context.resources.getString(R.string.raw_sound_4) -> {
                R.raw.desktop_computer
            }
            else -> null
        }

    }

    private fun init() {
        mMediaPlayer = MediaPlayer()
        val attrs = AudioAttributes.Builder()
        attrs.setUsage(AudioAttributes.USAGE_MEDIA)
        attrs.setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
        attrs.build()
        mMediaPlayer?.setOnPreparedListener {
            isReady= true
            mMediaPlayer?.start()
        }
        mMediaPlayer?.setOnErrorListener { _, _ , _->  false
        }
        mMediaPlayer?.setOnCompletionListener {
            mMediaPlayer?.stop()
            mMediaPlayer?.reset()
            isReady = false
        }
    }

     fun playSound(label : String) {
        val resource = getResources(label)
        if (resource != null) {
            val afd = context.resources.openRawResourceFd(resource)
            try {
                mMediaPlayer?.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
                if (!isReady) {
                    mMediaPlayer?.prepareAsync()
                } else {
                    if(mMediaPlayer?.isPlaying == true) {
                        mMediaPlayer?.pause()
                        mMediaPlayer?.stop()
                        mMediaPlayer?.reset()
                        isReady = false
                    }
                    else {
                        mMediaPlayer?.start()
                    }
                }
            }
            catch ( e : IOException ) {
             e.printStackTrace()
            }
            catch(e : IllegalStateException) {
                e.printStackTrace()
            }


        }
    }

    companion object {
        // Static Object ini
       private  var mMediaPlayer : MediaPlayer? = null
    }
}