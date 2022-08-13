package com.devtedi.tedi.utils

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import com.devtedi.tedi.R
import java.io.IOException

class SoundPlayer(private val context : Context ) {

    init {
        init()
    }
    private var isReady : Boolean = false
    private fun getResources(label : String )  : Int? {
        return when (label) {
            context.resources.getString(R.string.raw_sound_orang) -> {
                R.raw.orang
            }
            context.resources.getString(R.string.raw_sound_buah) -> {
                R.raw.buah
            }
            context.resources.getString(R.string.raw_sound_motor) -> {
                R.raw.motor
            }
            context.resources.getString(R.string.raw_sound_bunga) -> {
                R.raw.bunga
            }
            context.resources.getString(R.string.raw_sound_kucing) -> {
                R.raw.kucing
            }
            context.resources.getString(R.string.raw_sound_anjing) -> {
                R.raw.anjing
            }
            context.resources.getString(R.string.raw_sound_mobil) -> R.raw.anjing
            context.resources.getString(R.string.raw_sound_4) -> {
                R.raw.desktop_computer
            }
            context.resources.getString(R.string.raw_sound_seribu) -> {
                R.raw.seribu
            }
            context.resources.getString(R.string.raw_sound_duaribu) -> {
                R.raw.duaribu
            }
            context.resources.getString(R.string.raw_sound_limaribu) -> {
                R.raw.limaribu
            }
            context.resources.getString(R.string.raw_sound_sepuluhribu) -> {
                R.raw.sepuluhribu
            }
            context.resources.getString(R.string.raw_sound_limapuluhribu) -> {
                R.raw.limapuluhribu
            }
            context.resources.getString(R.string.raw_sound_seratusribu) -> {
                R.raw.seratusribu
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