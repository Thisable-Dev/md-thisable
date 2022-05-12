package com.bintangpoetra.thisable.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bintangpoetra.thisable.databinding.ItemLayoutDialogBinding
import com.bintangpoetra.thisable.interfaces.ObjectOptionInterface

class ObjectOptionAdapter(private val listOption: Array<String>) : RecyclerView.Adapter<ObjectOptionAdapter.ViewHolder>(){
    private var onClickSubscriber : ObjectOptionInterface? = null

    fun setOnClickItemListener (onClickSubscriber : ObjectOptionInterface) {
        this.onClickSubscriber = onClickSubscriber
    }

    inner class ViewHolder(private val binding : ItemLayoutDialogBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data : String ) {
            binding.tvItemDialog.text = data
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ObjectOptionAdapter.ViewHolder {
        val inflater = ItemLayoutDialogBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(inflater)
    }

    override fun onBindViewHolder(holder: ObjectOptionAdapter.ViewHolder, position: Int) {
        holder.bind(listOption[position])
        holder.itemView.setOnClickListener {
            onClickSubscriber?.onClick(listOption[position])
        }
        holder.itemView.setOnLongClickListener {
            onClickSubscriber?.onLongClickListener(listOption[position])
            true
        }
    }

    override fun getItemCount(): Int {
        return listOption.size
    }

}
