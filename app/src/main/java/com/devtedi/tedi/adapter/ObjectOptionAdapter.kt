package com.devtedi.tedi.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.devtedi.tedi.databinding.ItemLayoutDialogObjBinding
import com.devtedi.tedi.databinding.ItemQuestionsDialogCoreLayoutBinding
import com.devtedi.tedi.interfaces.ObjectOptionInterface

/**
 *
 * Kelas ini digunakan sebagai adapter RecyclerView, yang menampilkan list String/Question.
 *
 * @property listQuestions list dari String/Question yang digunakan.
 * @property onLongClick callback yang akan dieksekusi ketika item diclick lama.
 * @constructor untuk buat instance dari ObjectOptionsAdapter.
 */
class ObjectOptionsAdapter(
    private val listQuestions: Array<String>, private val onClick: (value: String) -> Unit,
    private val onLongClick: (value: String) -> Unit,
) : RecyclerView.Adapter<ObjectOptionsAdapter.ViewHolder>() {

    /**
     *
     * Kelas ini digunakan sebagai ViewHolder yang menampung view untuk setiap item di RecyclerView.
     *
     * @property binding class hasil generate dari viewBinding untuk item_questions_dialog_core_layout.xml.
     * @constructor untuk buat instance dari ViewHolder.
     */
    inner class ViewHolder(private val binding: ItemQuestionsDialogCoreLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        /**
         * Fungsi untuk bind/memasukkan isi [data] ke properti yang ada pada view.
         * @param [data] data item cloud model
         */
        fun bind(data: String) {
            binding.tvQuestion.text = data
        }
    }

    /**
     * Fungsi untuk membuat object [ObjectOptionsAdapter.ViewHolder]
     *
     * @return object dari [ObjectOptionsAdapter.ViewHolder].
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = ItemQuestionsDialogCoreLayoutBinding.inflate(LayoutInflater.from(parent.context),
            parent,
            false)
        return ViewHolder(inflater)
    }

    /**
     * Fungsi ini akan terpanggil ketika ada item baru pada RecyclerView.
     * Sehingga Method [ObjectOptionsAdapter.ViewHolder.bind(String)] akan terpanggil.
     * @param [holder] ViewHolder untuk setiap View.
     * @param [position] posisi setiap item di list.
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val question: String = listQuestions[position]
        holder.bind(question)

        // Kalau Di Click doang kasih tau pilihannya, ini subscriber keknya
        holder.itemView.setOnClickListener {
            onClick(question)
        }

        // Kalau Di tekan lama Baru execute pilihannya juga {}
        holder.itemView.setOnLongClickListener {
            onLongClick(question)
            true
        }
    }

    /**
     * Fungsi yang dibutuhkan oleh RecyclerView untuk mengetahui jumlah data yang ada di list.
     * @return mengembalikan ukuran list data yang akan dipakai RecyclerView untuk merender item.
     */
    override fun getItemCount(): Int {
        return listQuestions.size
    }
}
