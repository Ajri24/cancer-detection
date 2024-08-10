package com.dicoding.picodiploma.adapter

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.picodiploma.mycamera.R
import com.example.myapplication.db.RepositoryClass
import com.example.myapplication.db.HistoryDB

class Hadapter(
    private val context: Context,
    private val repository: RepositoryClass,
) : RecyclerView.Adapter<Hadapter.ViewHolderClass>() {

    private val dataList: MutableList<HistoryDB> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderClass {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.items_2, parent, false)
        return ViewHolderClass(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolderClass, position: Int) {
        val currentItem = dataList[position]
        holder.bind(currentItem)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    fun setData(items: List<HistoryDB>) {
        dataList.clear()
        dataList.addAll(items)
        notifyDataSetChanged()
    }

    inner class ViewHolderClass(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        private val rvImage: ImageView = itemView.findViewById(R.id.image)
        private val rvTitle: TextView = itemView.findViewById(R.id.title)

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(data: HistoryDB) {
            Glide.with(itemView.context)
                .load(data.image)
                .into(rvImage)

            rvTitle.text = data.label
        }

        override fun onClick(v: View?) {
            showContextMenu(adapterPosition)
        }

        private fun showContextMenu(position: Int) {
            val contextMenuItems = arrayOf("Edit", "Delete")

            val builder = AlertDialog.Builder(itemView.context)
            builder.setItems(contextMenuItems) { dialog, which ->
                when (which) {
                    0 -> {
                        showEditDialog()
                    }
                    1 -> {
                        val currentData = dataList[position]
                        repository.deleteHistory(currentData.id ?: 0L)
                        removeItem(position)
                    }
                }
            }
            builder.create().show()
        }

        private fun showEditDialog() {
            val currentData = dataList[adapterPosition]

            val builder = AlertDialog.Builder(itemView.context)
            builder.setTitle("Edit Name")

            val input = EditText(itemView.context)
            input.setText(currentData.label)
            builder.setView(input)

            builder.setPositiveButton("Save") { dialog, which ->
                val newName = input.text.toString()

                repository.updateHistoryLabel(currentData.id ?: 0L, newName)

                rvTitle.text = newName
            }

            builder.setNegativeButton("Cancel") { dialog, which ->
                dialog.cancel()
            }

            builder.show()
        }

        private fun removeItem(position: Int) {
            dataList.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, dataList.size)
        }
    }
}