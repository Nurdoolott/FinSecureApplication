package com.example.finsecureapp.ui.auth.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.finsecureapp.data.local.datastore.TokenManager
import com.example.finsecureapp.data.repository.AuthRepository
import com.example.finsecureapp.databinding.ActivityLoginBinding
import com.example.finsecureapp.ui.auth.register.RegisterActivity
import com.example.finsecureapp.utils.Resource
import com.example.finsecureapp.viewmodel.AuthViewModel
import com.example.finsecureapp.viewmodel.AuthViewModelFactory
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    private val viewModel: AuthViewModel by viewModels {
        AuthViewModelFactory(
            AuthRepository(),
            TokenManager(applicationContext)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener {
            val phoneNumber = binding.etPhoneNumber.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (phoneNumber.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            } else {
                viewModel.login(phoneNumber, password)
            }
        }

        binding.tvGoToRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
        binding.tvForgotPassword.setOnClickListener {
            startActivity(Intent(this, com.example.finsecureapp.ui.auth.forgot.ForgotPasswordActivity::class.java))
        }

        observeLoginState()
    }

    private fun observeLoginState() {
        lifecycleScope.launch {
            viewModel.loginState.collect { state ->
                when (state) {
                    is Resource.Loading -> {
                        binding.progressBar.visibility = android.view.View.VISIBLE
                    }

                    is Resource.Success -> {
                        binding.progressBar.visibility = android.view.View.GONE
                        Toast.makeText(this@LoginActivity, "Login successful", Toast.LENGTH_SHORT).show()

                        startActivity(Intent(this@LoginActivity, com.example.finsecureapp.ui.home.HomeActivity::class.java))
                        finish()
                    }

                    is Resource.Error -> {
                        binding.progressBar.visibility = android.view.View.GONE
                        Toast.makeText(this@LoginActivity, state.message, Toast.LENGTH_LONG).show()
                    }

                    null -> Unit
                }
            }
        }
    }
}