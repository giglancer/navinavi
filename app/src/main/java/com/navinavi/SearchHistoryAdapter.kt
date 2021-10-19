package com.navinavi

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.navinavi.room.SearchHistoryEntity

class SearchHistoryAdapter(private val list: ArrayList<SearchHistoryEntity>): RecyclerView.Adapter<SearchHistoryAdapter.ViewHolder>() {

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val from = view.findViewById<TextView>(R.id.from)
        val to = view.findViewById<TextView>(R.id.to)
        val searchTypeDateTime = view.findViewById<TextView>(R.id.searchTypeDateTime)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SearchHistoryAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.search_histry_row, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: SearchHistoryAdapter.ViewHolder, position: Int) {
        val list = list[position]
        holder.from.text = list.from
        holder.to.text = list.to
        holder.searchTypeDateTime.text = list.searchTypeDateTime
    }

    override fun getItemCount(): Int {
        return list.size
    }
}