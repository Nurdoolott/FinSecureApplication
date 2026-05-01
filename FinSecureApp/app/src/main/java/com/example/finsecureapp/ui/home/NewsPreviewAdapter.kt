package com.example.finsecureapp.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.finsecureapp.data.remote.dto.ArticleDto
import com.example.finsecureapp.databinding.ItemNewsPreviewBinding

class NewsPreviewAdapter(
    private var items: List<ArticleDto> = emptyList(),
    private val onItemClick: () -> Unit
) : RecyclerView.Adapter<NewsPreviewAdapter.NewsPreviewViewHolder>() {

    inner class NewsPreviewViewHolder(
        private val binding: ItemNewsPreviewBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ArticleDto) {
            binding.tvNewsPreviewTitle.text = item.title ?: "No title"
            binding.tvNewsPreviewSource.text = item.source?.name ?: "Unknown source"

            Glide.with(binding.root.context)
                .load(item.urlToImage)
                .centerCrop()
                .into(binding.ivNewsPreview)

            binding.root.setOnClickListener {
                onItemClick()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsPreviewViewHolder {
        val binding = ItemNewsPreviewBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return NewsPreviewViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NewsPreviewViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    fun submitList(newItems: List<ArticleDto>) {
        items = newItems
        notifyDataSetChanged()
    }
}