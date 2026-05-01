package com.example.finsecureapp.ui.splash

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.finsecureapp.R
import com.example.finsecureapp.data.local.datastore.TokenManager
import com.example.finsecureapp.data.repository.UserRepository
import com.example.finsecureapp.ui.auth.login.LoginActivity
import com.example.finsecureapp.ui.home.HomeActivity
import com.example.finsecureapp.utils.Resource
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {

    private lateinit var tokenManager: TokenManager
    private val userRepository = UserRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        tokenManager = TokenManager(applicationContext)

        lifecycleScope.launch {
            val token = tokenManager.getToken().first()

            if (token.isNullOrEmpty()) {
                openLogin()
            } else {
                when (userRepository.getMe(token)) {
                    is Resource.Success -> openHome()
                    is Resource.Error -> {
                        tokenManager.clearToken()
                        openLogin()
                    }
                    is Resource.Loading -> Unit
                }
            }
        }
    }

    private fun openHome() {
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }

    private fun openLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}