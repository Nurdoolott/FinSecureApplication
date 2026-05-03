package com.example.finsecureapp.ui.auth.register

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.finsecureapp.data.local.datastore.TokenManager
import com.example.finsecureapp.data.repository.AuthRepository
import com.example.finsecureapp.databinding.ActivityRegisterBinding
import com.example.finsecureapp.ui.auth.login.LoginActivity
import com.example.finsecureapp.ui.auth.verify.VerifyOtpActivity
import com.example.finsecureapp.utils.Resource
import com.example.finsecureapp.viewmodel.AuthViewModel
import com.example.finsecureapp.viewmodel.AuthViewModelFactory
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var viewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(
            this,
            AuthViewModelFactory(AuthRepository(), TokenManager(applicationContext))
        )[AuthViewModel::class.java]

        binding.btnRegister.setOnClickListener {
            val fullName = binding.etFullName.text.toString().trim()
            val phoneNumber = binding.etPhoneNumber.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            val email = binding.etEmail.text.toString().trim().ifEmpty { null }

            if (fullName.isEmpty() || phoneNumber.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill required fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Сохраняем данные и отправляем OTP через Firebase
            viewModel.sendOtp(phoneNumber, this)

            // Сохраняем данные для передачи в VerifyOtpActivity
            viewModel.pendingFullName = fullName
            viewModel.pendingPassword = password
            viewModel.pendingEmail = email
            viewModel.pendingPhone = phoneNumber
        }

        binding.tvGoToLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        observeOtpSent()
    }

    private fun observeOtpSent() {
        lifecycleScope.launch {
            viewModel.otpSentState.collect { state ->
                when (state) {
                    is Resource.Loading -> binding.progressBar.visibility = View.VISIBLE
                    is Resource.Success -> {
                        binding.progressBar.visibility = View.GONE
                        val intent = Intent(this@RegisterActivity, VerifyOtpActivity::class.java)
                        intent.putExtra("mode", "register")
                        intent.putExtra("verificationId", viewModel.verificationId) // добавить
                        intent.putExtra("fullName", viewModel.pendingFullName)
                        intent.putExtra("password", viewModel.pendingPassword)
                        intent.putExtra("email", viewModel.pendingEmail)
                        startActivity(intent)
                    }
                    is Resource.Error -> {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(this@RegisterActivity, state.message, Toast.LENGTH_LONG).show()
                    }
                    null -> Unit
                }
            }
        }
    }
}