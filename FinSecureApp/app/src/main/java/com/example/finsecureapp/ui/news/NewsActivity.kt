package com.example.finsecureapp.ui.news

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.finsecureapp.data.repository.NewsRepository
import com.example.finsecureapp.databinding.ActivityNewsBinding
import com.example.finsecureapp.utils.Resource
import com.example.finsecureapp.viewmodel.NewsViewModel
import com.example.finsecureapp.viewmodel.NewsViewModelFactory
import kotlinx.coroutines.launch

class NewsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewsBinding
    private lateinit var viewModel: NewsViewModel
    private lateinit var adapter: NewsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = NewsAdapter { article ->
            val url = article.url
            if (!url.isNullOrEmpty()) {
                val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(url))
                startActivity(intent)
            } else {
                Toast.makeText(this, "Article link is unavailable", Toast.LENGTH_SHORT).show()
            }
        }
        binding.recyclerViewNews.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewNews.adapter = adapter

        viewModel = ViewModelProvider(
            this,
            NewsViewModelFactory(NewsRepository())
        )[NewsViewModel::class.java]

        binding.btnSearch.setOnClickListener {
            val query = binding.etSearch.text.toString().trim()

            if (query.isEmpty()) {
                viewModel.loadTopNews()
            } else {
                viewModel.searchNews(query)
            }
        }

        observeNews()
        viewModel.loadTopNews()
    }

    private fun observeNews() {
        lifecycleScope.launch {
            viewModel.newsState.collect { state ->
                when (state) {
                    is Resource.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }

                    is Resource.Success -> {
                        binding.progressBar.visibility = View.GONE
                        adapter.submitList(state.data)
                    }

                    is Resource.Error -> {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(this@NewsActivity, state.message, Toast.LENGTH_LONG).show()
                    }

                    null -> Unit
                }
            }
        }
    }
}