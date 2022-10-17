package com.devtedi.tedi.utils

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import com.devtedi.tedi.R
import java.io.IOException

 class SoundPlayer private constructor(private val context : Context ) {

    init {
        init()
    }
    private var isReady : Boolean = false
    private fun getResources(label : String )  : Int? {
        return when (label) {


            // *********** Object Dewtection Resources !! ***************************///
            context.resources.getString(R.string.raw_soundobj_orang) -> {
                R.raw.orang
            }
            context.resources.getString(R.string.raw_soundobj_sepeda) -> {
                R.raw.buah
            }
            context.resources.getString(R.string.raw_soundobj_mobil) -> {
                R.raw.motor
            }

            context.resources.getString(R.string.raw_soundobj_sepedamotor) -> {
                R.raw.bunga
            }
            context.resources.getString(R.string.raw_soundobj_pesawat_terbang) -> {
                R.raw.kucing
            }
            context.resources.getString(R.string.raw_soundobj_bis) -> {
                R.raw.anjing
            }
            context.resources.getString(R.string.raw_soundobj_kereta) -> R.raw.anjing


            context.resources.getString(R.string.raw_soundobj_truk) -> R.raw.anjing
            context.resources.getString(R.string.raw_soundobj_kapal) -> R.raw.anjing
            context.resources.getString(R.string.raw_soundobj_lampu_lalu_lintas) -> R.raw.anjing
            context.resources.getString(R.string.raw_soundobj_keran_kebakaran) -> R.raw.anjing
            context.resources.getString(R.string.raw_soundobj_tanda_berhenti) -> R.raw.anjing
            context.resources.getString(R.string.raw_soundobj_meteran_parkir) -> R.raw.anjing
            context.resources.getString(R.string.raw_soundobj_bangku) -> R.raw.anjing
            context.resources.getString(R.string.raw_soundobj_burung) -> R.raw.anjing
            context.resources.getString(R.string.raw_soundobj_kucing) -> R.raw.anjing
            context.resources.getString(R.string.raw_soundobj_anjing) -> R.raw.anjing
            context.resources.getString(R.string.raw_soundobj_kuda) -> R.raw.anjing
            context.resources.getString(R.string.raw_soundobj_domba) -> R.raw.anjing
            context.resources.getString(R.string.raw_soundobj_lembu) -> R.raw.anjing
            context.resources.getString(R.string.raw_soundobj_gajah) -> R.raw.anjing
            context.resources.getString(R.string.raw_soundobj_beruang) -> R.raw.anjing
            context.resources.getString(R.string.raw_soundobj_zebra) -> R.raw.anjing
            context.resources.getString(R.string.raw_soundobj_jerapah) -> R.raw.anjing
            context.resources.getString(R.string.raw_soundobj_ransel) -> R.raw.anjing
            context.resources.getString(R.string.raw_soundobj_payung) -> R.raw.anjing
            context.resources.getString(R.string.raw_soundobj_tas) -> R.raw.anjing
            context.resources.getString(R.string.raw_soundobj_dasi) -> R.raw.anjing
            context.resources.getString(R.string.raw_soundobj_koper) -> R.raw.anjing
            context.resources.getString(R.string.raw_soundobj_frisbee) -> R.raw.anjing
            context.resources.getString(R.string.raw_soundobj_ski) -> R.raw.anjing
            context.resources.getString(R.string.raw_soundobj_papan_seluncur) -> R.raw.anjing
            context.resources.getString(R.string.raw_soundobj_bola_olahraga) -> R.raw.anjing
            context.resources.getString(R.string.raw_soundobj_layang_layang) -> R.raw.anjing
            context.resources.getString(R.string.raw_soundobj_tongkat_bisbol) -> R.raw.anjing
            context.resources.getString(R.string.raw_soundobj_sarung_bisbol) -> R.raw.anjing
            context.resources.getString(R.string.raw_soundobj_skateboard) -> R.raw.anjing
            context.resources.getString(R.string.raw_soundobj_raket_tenis) -> R.raw.anjing
            context.resources.getString(R.string.raw_soundobj_botol) -> R.raw.anjing
            context.resources.getString(R.string.raw_soundobj_gelas_anggur) -> R.raw.anjing
            context.resources.getString(R.string.raw_soundobj_cangkir) -> R.raw.anjing
            context.resources.getString(R.string.raw_soundobj_garpu) -> R.raw.anjing
            context.resources.getString(R.string.raw_soundobj_mangkuk) -> R.raw.anjing
            context.resources.getString(R.string.raw_soundobj_pisang) -> R.raw.anjing
            context.resources.getString(R.string.raw_soundobj_manzana) -> R.raw.anjing
            context.resources.getString(R.string.raw_soundobj_sandwich) -> R.raw.anjing
            context.resources.getString(R.string.raw_soundobj_jeruk) -> R.raw.anjing
            context.resources.getString(R.string.raw_soundobj_brokoli) -> R.raw.anjing
            context.resources.getString(R.string.raw_soundobj_wortel) -> R.raw.anjing
            context.resources.getString(R.string.raw_soundobj_hot_dog) -> R.raw.anjing
            context.resources.getString(R.string.raw_soundobj_pizza) -> R.raw.anjing
            context.resources.getString(R.string.raw_soundobj_donat) -> R.raw.anjing
            context.resources.getString(R.string.raw_soundobj_kue) -> R.raw.anjing
            context.resources.getString(R.string.raw_soundobj_kursi) -> R.raw.anjing
            context.resources.getString(R.string.raw_soundobj_sofa) -> R.raw.anjing
            context.resources.getString(R.string.raw_soundobj_tanamanpot) -> R.raw.anjing
            context.resources.getString(R.string.raw_soundobj_tempat_tidur) -> R.raw.anjing
            context.resources.getString(R.string.raw_soundobj_meja_makan) -> R.raw.anjing
            context.resources.getString(R.string.raw_soundobj_toilet) -> R.raw.anjing
            context.resources.getString(R.string.raw_soundobj_laptop) -> R.raw.anjing
            context.resources.getString(R.string.raw_soundobj_mouse) -> R.raw.anjing
            context.resources.getString(R.string.raw_soundobj_terpencil) -> R.raw.anjing
            context.resources.getString(R.string.raw_soundobj_papan_ketik) -> R.raw.anjing
            context.resources.getString(R.string.raw_soundobj_gawai) -> R.raw.anjing
            context.resources.getString(R.string.raw_soundobj_microwave) -> R.raw.anjing
            context.resources.getString(R.string.raw_soundobj_oven) -> R.raw.anjing
            context.resources.getString(R.string.raw_soundobj_pemanggang_roti) -> R.raw.anjing
            context.resources.getString(R.string.raw_soundobj_wastavel) -> R.raw.anjing
            context.resources.getString(R.string.raw_soundobj_kulkas) -> R.raw.anjing
            context.resources.getString(R.string.raw_soundobj_buku) -> R.raw.anjing
            context.resources.getString(R.string.raw_soundobj_jam) -> R.raw.anjing
            context.resources.getString(R.string.raw_soundobj_vas_bunga) -> R.raw.anjing
            context.resources.getString(R.string.raw_soundobj_gunting) -> R.raw.anjing
            context.resources.getString(R.string.raw_soundobj_boneka_beruang) -> R.raw.anjing
            context.resources.getString(R.string.raw_soundobj_pengering_rambut) -> R.raw.anjing
            context.resources.getString(R.string.raw_soundobj_sikat_gigi) -> R.raw.anjing


            //************ Currency Detection Resources **************************//

            context.resources.getString(R.string.raw_soundcurrency_seribu) -> {
                R.raw.seribu
            }
            context.resources.getString(R.string.raw_soundcurrency_duaribu) -> {
                R.raw.duaribu
            }
            context.resources.getString(R.string.raw_soundcurrency_limaribu) -> {
                R.raw.limaribu
            }
            context.resources.getString(R.string.raw_soundcurrency_sepuluhribu) -> {
                R.raw.sepuluhribu
            }
            context.resources.getString(R.string.raw_soundcurrency_limapuluhribu) -> {
                R.raw.limapuluhribu
            }
            context.resources.getString(R.string.raw_soundcurrency_seratusribu) -> {
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
       @Volatile
       private var instance : SoundPlayer? = null

        @JvmStatic
        fun getInstance(context: Context)  : SoundPlayer
        {
            if(instance == null)
            {
                synchronized(this)
                {
                    instance = SoundPlayer(context.applicationContext)
                }
            }
            return instance as SoundPlayer
        }
       private  var mMediaPlayer : MediaPlayer? = null
    }
}