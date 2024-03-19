package com.example.springbreak

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class LanguagesAdapter(private val languages: List<String>, private val onClick: (String) -> Unit) : RecyclerView.Adapter<LanguagesAdapter.LanguageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LanguageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(android.R.layout.simple_list_item_1, parent, false)
        return LanguageViewHolder(view)
    }

    override fun onBindViewHolder(holder: LanguageViewHolder, position: Int) {
        val language = languages[position]
        holder.languageName.text = language
        holder.itemView.setOnClickListener { onClick(language) }
    }

    override fun getItemCount() = languages.size

    class LanguageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val languageName: TextView = itemView.findViewById(android.R.id.text1)
    }
}