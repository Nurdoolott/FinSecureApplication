package com.example.finsecureapp.ui.history

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.finsecureapp.data.local.datastore.TokenManager
import com.example.finsecureapp.data.repository.TransactionRepository
import com.example.finsecureapp.databinding.ActivityHistoryBinding
import com.example.finsecureapp.utils.Resource
import com.example.finsecureapp.viewmodel.HistoryViewModel
import com.example.finsecureapp.viewmodel.HistoryViewModelFactory
import kotlinx.coroutines.launch

class HistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHistoryBinding
    private lateinit var viewModel: HistoryViewModel
    private lateinit var adapter: TransactionHistoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = TransactionHistoryAdapter()
        binding.recyclerViewHistory.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewHistory.adapter = adapter
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.loadHistory()
        }

        viewModel = ViewModelProvider(
            this,
            HistoryViewModelFactory(
                TransactionRepository(),
                TokenManager(applicationContext)
            )
        )[HistoryViewModel::class.java]

        observeHistory()
        viewModel.loadHistory()
    }

    private fun observeHistory() {
        lifecycleScope.launch {
            viewModel.historyState.collect { state ->
                when (state) {
                    is Resource.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }

                    is Resource.Success -> {
                        binding.progressBar.visibility = View.GONE
                        binding.swipeRefreshLayout.isRefreshing = false
                        adapter.submitList(state.data)
                    }

                    is Resource.Error -> {
                        binding.progressBar.visibility = View.GONE
                        binding.swipeRefreshLayout.isRefreshing = false
                        Toast.makeText(this@HistoryActivity, state.message, Toast.LENGTH_LONG).show()
                    }

                    null -> Unit
                }
            }
        }
    }
}