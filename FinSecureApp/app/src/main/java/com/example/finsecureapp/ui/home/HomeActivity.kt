package com.example.finsecureapp.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.finsecureapp.data.local.datastore.TokenManager
import com.example.finsecureapp.data.repository.AccountRepository
import com.example.finsecureapp.data.repository.NewsRepository
import com.example.finsecureapp.databinding.ActivityHomeBinding
import com.example.finsecureapp.ui.history.HistoryActivity
import com.example.finsecureapp.ui.news.NewsActivity
import com.example.finsecureapp.ui.profile.ProfileActivity
import com.example.finsecureapp.ui.transfer.TransferActivity
import com.example.finsecureapp.utils.Resource
import com.example.finsecureapp.viewmodel.AccountViewModel
import com.example.finsecureapp.viewmodel.AccountViewModelFactory
import com.example.finsecureapp.viewmodel.NewsViewModel
import com.example.finsecureapp.viewmodel.NewsViewModelFactory
import kotlinx.coroutines.launch
import com.example.finsecureapp.data.repository.UserRepository
import com.example.finsecureapp.viewmodel.UserViewModel
import com.example.finsecureapp.viewmodel.UserViewModelFactory

class HomeActivity : AppCompatActivity() {

    private lateinit var userViewModel: UserViewModel
    private lateinit var binding: ActivityHomeBinding
    private lateinit var accountViewModel: AccountViewModel
    private lateinit var newsViewModel: NewsViewModel
    private var fullAccountNumber: String = ""
    private var isAccountExpanded = false

    private var currentFullName: String = ""
    private var currentPhoneNumber: String = ""
    private var currentEmail: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        accountViewModel = ViewModelProvider(
            this,
            AccountViewModelFactory(
                AccountRepository(),
                TokenManager(applicationContext)
            )
        )[AccountViewModel::class.java]

        newsViewModel = ViewModelProvider(
            this,
            NewsViewModelFactory(NewsRepository())
        )[NewsViewModel::class.java]

        userViewModel = ViewModelProvider(
            this,
            UserViewModelFactory(
                UserRepository(),
                TokenManager(applicationContext)
            )
        )[UserViewModel::class.java]

        val previewAdapter = NewsPreviewAdapter {
            startActivity(Intent(this, NewsActivity::class.java))
        }
        binding.viewPagerNewsPreview.adapter = previewAdapter

        observeUserProfile()
        userViewModel.loadUserProfile()

        binding.swipeRefreshLayout.setOnRefreshListener {
            accountViewModel.loadBalance()
            newsViewModel.loadTopNews()
        }

        binding.btnTransfer.setOnClickListener {
            startActivity(Intent(this, TransferActivity::class.java))
        }

        binding.btnHistory.setOnClickListener {
            startActivity(Intent(this, HistoryActivity::class.java))
        }

        binding.btnProfile.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java).apply {
                putExtra("fullName", currentFullName)
                putExtra("phoneNumber", currentPhoneNumber)
                putExtra("email", currentEmail)
            }
            startActivity(intent)
        }

        binding.newsPreviewCard.setOnClickListener {
            startActivity(Intent(this, NewsActivity::class.java))
        }

        binding.accountCard.setOnClickListener {
            toggleAccountNumber()
        }

        observeBalanceState()
        observeNewsPreview(previewAdapter)

        accountViewModel.loadBalance()
        newsViewModel.loadTopNews()
    }

    override fun onResume() {
        super.onResume()
        accountViewModel.loadBalance()
    }

    private fun observeUserProfile() {
        lifecycleScope.launch {
            userViewModel.userState.collect { state ->
                when (state) {
                    is Resource.Success -> {
                        currentFullName = state.data.fullName
                        currentPhoneNumber = state.data.phoneNumber
                        currentEmail = state.data.email ?: ""
                    }

                    is Resource.Error -> {
                        Toast.makeText(this@HomeActivity, state.message, Toast.LENGTH_LONG).show()
                    }

                    is Resource.Loading -> Unit
                    null -> Unit
                }
            }
        }
    }

    private fun observeBalanceState() {
        lifecycleScope.launch {
            accountViewModel.balanceState.collect { state ->
                when (state) {
                    is Resource.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }

                    is Resource.Success -> {
                        binding.progressBar.visibility = View.GONE
                        binding.swipeRefreshLayout.isRefreshing = false

                        fullAccountNumber = state.data.accountNumber
                        binding.tvAccountNumber.text = formatAccountNumber()
                        binding.tvBalance.text = "${state.data.balance} SOM"
                        binding.tvCurrency.text = "Currency: SOM"
                    }

                    is Resource.Error -> {
                        binding.progressBar.visibility = View.GONE
                        binding.swipeRefreshLayout.isRefreshing = false
                        Toast.makeText(this@HomeActivity, state.message, Toast.LENGTH_LONG).show()
                    }

                    null -> Unit
                }
            }
        }
    }

    private fun observeNewsPreview(adapter: NewsPreviewAdapter) {
        lifecycleScope.launch {
            newsViewModel.newsState.collect { state ->
                when (state) {
                    is Resource.Loading -> Unit

                    is Resource.Success -> {
                        binding.swipeRefreshLayout.isRefreshing = false
                        adapter.submitList(state.data.take(5))
                    }

                    is Resource.Error -> {
                        binding.swipeRefreshLayout.isRefreshing = false
                    }

                    null -> Unit
                }
            }
        }
    }

    private fun formatAccountNumber(): String {
        if (fullAccountNumber.isBlank()) return "Account Number: ..."
        return if (isAccountExpanded) {
            "Account Number: $fullAccountNumber"
        } else {
            val lastThree = fullAccountNumber.takeLast(3)
            "Account Number: ACC***$lastThree"
        }
    }

    private fun toggleAccountNumber() {
        isAccountExpanded = !isAccountExpanded
        binding.tvAccountNumber.text = formatAccountNumber()
    }

}