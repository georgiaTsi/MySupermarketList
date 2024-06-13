package com.example.mysupermarketlist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

interface OnItemClickListener{
    fun onItemClick(item: ListAdapter.ListItem)
}

class ListAdapter(private var dataSet: List<ListItem>, private val onItemClickListener: OnItemClickListener) : RecyclerView.Adapter<ListAdapter.ViewHolder>() {

    data class ListItem(val title: String, var isChecked: Boolean)

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val textView: TextView = view.findViewById(R.id.textView)
        val checkBox: CheckBox = view.findViewById(R.id.checkbox)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
       val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.text_row_item, viewGroup, false)

        return ViewHolder(view)
    }

    override fun getItemCount() = dataSet.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textView.text = dataSet[position].title
        holder.checkBox.isChecked = dataSet[position].isChecked

        holder.itemView.setOnClickListener{
            dataSet[position].isChecked = !dataSet[position].isChecked

            onItemClickListener.onItemClick(dataSet[position])

            notifyItemChanged(position)
        }
    }

    fun updateAdapter(newDataSet: List<ListItem>) {
        dataSet = newDataSet

        notifyDataSetChanged()
    }
}
