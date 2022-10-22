package com.devtedi.tedi.utils

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import com.devtedi.tedi.R
import java.io.IOException

class SoundPlayer private constructor(private val context: Context) {

    private var isReady: Boolean = false

    init {
        initMediaPlayer()
    }

    private fun getResources(label: String): Int? {
        return when (label) {

            // *********** Object Detection Resources *********** //
            context.resources.getString(R.string.raw_soundobj_orang) -> {
                R.raw.orang
            }
            context.resources.getString(R.string.raw_soundobj_sepeda) -> {
                R.raw.sepeda
            }
            context.resources.getString(R.string.raw_soundobj_mobil) -> {
                R.raw.mobil
            }

            context.resources.getString(R.string.raw_soundobj_sepedamotor) -> {
                R.raw.sepedamotor
            }
            context.resources.getString(R.string.raw_soundobj_pesawat_terbang) -> {
                R.raw.pesawatterbang
            }
            context.resources.getString(R.string.raw_soundobj_bis) -> {
                R.raw.bis
            }
            context.resources.getString(R.string.raw_soundobj_kereta) -> R.raw.kereta


            context.resources.getString(R.string.raw_soundobj_truk) -> R.raw.truk
            context.resources.getString(R.string.raw_soundobj_kapal) -> R.raw.kapal
            context.resources.getString(R.string.raw_soundobj_lampu_lalu_lintas) -> R.raw.lampulalulintas
            context.resources.getString(R.string.raw_soundobj_keran_kebakaran) -> R.raw.kerankebakaran
            context.resources.getString(R.string.raw_soundobj_tanda_berhenti) -> R.raw.tandaberhenti
            context.resources.getString(R.string.raw_soundobj_meteran_parkir) -> R.raw.meteranparkir
            context.resources.getString(R.string.raw_soundobj_bangku) -> R.raw.bangku
            context.resources.getString(R.string.raw_soundobj_burung) -> R.raw.burung
            context.resources.getString(R.string.raw_soundobj_kucing) -> R.raw.kucing
            context.resources.getString(R.string.raw_soundobj_anjing) -> R.raw.anjing
            context.resources.getString(R.string.raw_soundobj_kuda) -> R.raw.kuda
            context.resources.getString(R.string.raw_soundobj_domba) -> R.raw.domba
            context.resources.getString(R.string.raw_soundobj_lembu) -> R.raw.lembu
            context.resources.getString(R.string.raw_soundobj_gajah) -> R.raw.gajah
            context.resources.getString(R.string.raw_soundobj_beruang) -> R.raw.beruang
            context.resources.getString(R.string.raw_soundobj_zebra) -> R.raw.zebra
            context.resources.getString(R.string.raw_soundobj_jerapah) -> R.raw.jerapah
            context.resources.getString(R.string.raw_soundobj_ransel) -> R.raw.ransel
            context.resources.getString(R.string.raw_soundobj_payung) -> R.raw.payung
            context.resources.getString(R.string.raw_soundobj_tas) -> R.raw.tas
            context.resources.getString(R.string.raw_soundobj_dasi) -> R.raw.dasi
            context.resources.getString(R.string.raw_soundobj_koper) -> R.raw.koper
            context.resources.getString(R.string.raw_soundobj_frisbee) -> R.raw.frisbee
            context.resources.getString(R.string.raw_soundobj_ski) -> R.raw.ski
            context.resources.getString(R.string.raw_soundobj_papan_seluncur) -> R.raw.papanseluncur
            context.resources.getString(R.string.raw_soundobj_bola_olahraga) -> R.raw.bolaolahraga
            context.resources.getString(R.string.raw_soundobj_layang_layang) -> R.raw.layanglayang
            context.resources.getString(R.string.raw_soundobj_tongkat_bisbol) -> R.raw.tongkatbisbol
            context.resources.getString(R.string.raw_soundobj_sarung_bisbol) -> R.raw.sarungtanganbisbol
            context.resources.getString(R.string.raw_soundobj_skateboard) -> R.raw.skateboard
            context.resources.getString(R.string.raw_soundobj_raket_tenis) -> R.raw.rakettenis
            context.resources.getString(R.string.raw_soundobj_botol) -> R.raw.botol
            context.resources.getString(R.string.raw_soundobj_gelas_anggur) -> R.raw.gelasanggur
            context.resources.getString(R.string.raw_soundobj_cangkir) -> R.raw.cangkir
            context.resources.getString(R.string.raw_soundobj_garpu) -> R.raw.garpu
            context.resources.getString(R.string.raw_soundobj_mangkuk) -> R.raw.mangkuk
            context.resources.getString(R.string.raw_soundobj_pisang) -> R.raw.pisang
            context.resources.getString(R.string.raw_soundobj_manzana) -> R.raw.manzana
            context.resources.getString(R.string.raw_soundobj_sandwich) -> R.raw.sandwich
            context.resources.getString(R.string.raw_soundobj_jeruk) -> R.raw.jeruk
            context.resources.getString(R.string.raw_soundobj_brokoli) -> R.raw.brokoli
            context.resources.getString(R.string.raw_soundobj_wortel) -> R.raw.wortel
            context.resources.getString(R.string.raw_soundobj_hot_dog) -> R.raw.hotdog
            context.resources.getString(R.string.raw_soundobj_pizza) -> R.raw.pizza
            context.resources.getString(R.string.raw_soundobj_donat) -> R.raw.donat
            context.resources.getString(R.string.raw_soundobj_kue) -> R.raw.kue
            context.resources.getString(R.string.raw_soundobj_kursi) -> R.raw.kursi
            context.resources.getString(R.string.raw_soundobj_sofa) -> R.raw.sofa
            context.resources.getString(R.string.raw_soundobj_tanamanpot) -> R.raw.tanamanpot
            context.resources.getString(R.string.raw_soundobj_tempat_tidur) -> R.raw.tempattidur
            context.resources.getString(R.string.raw_soundobj_meja_makan) -> R.raw.mejamakan
            context.resources.getString(R.string.raw_soundobj_toilet) -> R.raw.toilet
            context.resources.getString(R.string.raw_soundobj_laptop) -> R.raw.laptop
            context.resources.getString(R.string.raw_soundobj_mouse) -> R.raw.mouse
            context.resources.getString(R.string.raw_soundobj_terpencil) -> R.raw.terpencil
            context.resources.getString(R.string.raw_soundobj_papan_ketik) -> R.raw.papanketik
            context.resources.getString(R.string.raw_soundobj_gawai) -> R.raw.gawai
            context.resources.getString(R.string.raw_soundobj_microwave) -> R.raw.microwave
            context.resources.getString(R.string.raw_soundobj_oven) -> R.raw.oven
            context.resources.getString(R.string.raw_soundobj_pemanggang_roti) -> R.raw.pemanggangroti
            context.resources.getString(R.string.raw_soundobj_wastavel) -> R.raw.wastafel
            context.resources.getString(R.string.raw_soundobj_kulkas) -> R.raw.kulkas
            context.resources.getString(R.string.raw_soundobj_buku) -> R.raw.buku
            context.resources.getString(R.string.raw_soundobj_jam) -> R.raw.jam
            context.resources.getString(R.string.raw_soundobj_vas_bunga) -> R.raw.vasbunga
            context.resources.getString(R.string.raw_soundobj_gunting) -> R.raw.gunting
            context.resources.getString(R.string.raw_soundobj_boneka_beruang) -> R.raw.bonekaberuang
            context.resources.getString(R.string.raw_soundobj_pengering_rambut) -> R.raw.pengeringrambut
            context.resources.getString(R.string.raw_soundobj_sikat_gigi) -> R.raw.sikatgigi


            // *********** Currency Detection Resources *********** //
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

    private fun initMediaPlayer() {
        mMediaPlayer = MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ASSISTANCE_ACCESSIBILITY)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
            setOnPreparedListener {
                isReady = true
                mMediaPlayer?.start()
            }
            setOnCompletionListener {
                mMediaPlayer?.stop()
                mMediaPlayer?.reset()
                isReady = false
            }
        }
    }

    fun playSound(label: String) {
        val resource = getResources(label)
        if (resource != null) {
            val afd = context.resources.openRawResourceFd(resource)
            try {
                mMediaPlayer?.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
                if (!isReady) {
                    mMediaPlayer?.prepareAsync()
                } else {
                    if (mMediaPlayer?.isPlaying == true) {
                        mMediaPlayer?.pause()
                        mMediaPlayer?.stop()
                        mMediaPlayer?.reset()
                        isReady = false
                    } else {
                        mMediaPlayer?.start()
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: IllegalStateException) {
                e.printStackTrace()
            }
        }
    }

    fun dispose() {
        mMediaPlayer?.release()
        mMediaPlayer = null
    }

    companion object {
        // Static Object ini
        @Volatile
        private var instance: SoundPlayer? = null

        @JvmStatic
        fun getInstance(context: Context): SoundPlayer {
            if (instance == null) {
                synchronized(this)
                {
                    instance = SoundPlayer(context.applicationContext)
                }
            }
            return instance as SoundPlayer
        }

        private var mMediaPlayer: MediaPlayer? = null
    }
}