package com.devtedi.tedi.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.devtedi.tedi.databinding.ItemLayoutDialogObjBinding
import com.devtedi.tedi.interfaces.ObjectOptionInterface


class ObjectOptionsAdapter(
    private val listQuestions: Array<String>, private val onClick: (value: String) -> Unit,
    private val onLongClick: (value: String) -> Unit,
) : RecyclerView.Adapter<ObjectOptionsAdapter.ViewHolder>() {
    inner class ViewHolder(private val binding: ItemQuestionDialogLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: String) {
            binding.tvQuestion.text = data
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = ItemQuestionDialogLayoutBinding.inflate(LayoutInflater.from(parent.context),
            parent,
            false)
        return ViewHolder(inflater)
    }

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

    override fun getItemCount(): Int {
        return listQuestions.size
    }
}
