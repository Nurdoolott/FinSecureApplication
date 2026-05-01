package com.example.finsecureapp.ui.news

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.finsecureapp.data.remote.dto.ArticleDto
import com.example.finsecureapp.databinding.ItemNewsBinding

class NewsAdapter(
    private var items: List<ArticleDto> = emptyList(),
    private val onItemClick: (ArticleDto) -> Unit
) : RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

    inner class NewsViewHolder(
        private val binding: ItemNewsBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ArticleDto) {
            binding.tvTitle.text = item.title ?: "No title"
            binding.tvDescription.text = item.description ?: "No description"
            binding.tvSource.text = item.source?.name ?: "Unknown source"
            binding.tvDate.text = item.publishedAt ?: "No date"

            Glide.with(binding.root.context)
                .load(item.urlToImage)
                .centerCrop()
                .into(binding.ivNews)

            binding.root.setOnClickListener {
                onItemClick(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val binding = ItemNewsBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return NewsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    fun submitList(newItems: List<ArticleDto>) {
        items = newItems
        notifyDataSetChanged()
    }
}