package com.example.finsecureapp.ui.auth.verify

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.finsecureapp.data.local.datastore.TokenManager
import com.example.finsecureapp.data.repository.AuthRepository
import com.example.finsecureapp.databinding.ActivityVerifyOtpBinding
import com.example.finsecureapp.ui.auth.login.LoginActivity
import com.example.finsecureapp.utils.Resource
import com.example.finsecureapp.viewmodel.AuthViewModel
import com.example.finsecureapp.viewmodel.AuthViewModelFactory
import kotlinx.coroutines.launch

class VerifyOtpActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVerifyOtpBinding
    private lateinit var viewModel: AuthViewModel
    private var mode: String = "register" // "register" или "forgot"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVerifyOtpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mode = intent.getStringExtra("mode") ?: "register"

        // Получаем все данные из Intent
        val verificationId = intent.getStringExtra("verificationId")
        val fullName = intent.getStringExtra("fullName")
        val password = intent.getStringExtra("password")
        val email = intent.getStringExtra("email")

        if (mode == "forgot") {
            binding.layoutNewPassword.visibility = View.VISIBLE
        }

        viewModel = ViewModelProvider(
            this,
            AuthViewModelFactory(AuthRepository(), TokenManager(applicationContext))
        )[AuthViewModel::class.java]

        // Устанавливаем данные в ViewModel
        viewModel.verificationId = verificationId
        viewModel.pendingFullName = fullName
        viewModel.pendingPassword = password
        viewModel.pendingEmail = email

        binding.btnVerifyOtp.setOnClickListener {
            val otpCode = binding.etOtpCode.text.toString().trim()
            if (otpCode.isEmpty()) {
                Toast.makeText(this, "Enter OTP code", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (mode == "register") {
                viewModel.verifyOtpAndRegister(
                    otpCode = otpCode,
                    fullName = fullName ?: "",
                    password = password ?: "",
                    email = email
                )
            } else {
                val newPassword = binding.etNewPassword.text.toString().trim()
                if (newPassword.isEmpty()) {
                    Toast.makeText(this, "Enter new password", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                viewModel.verifyOtpAndResetPassword(otpCode, newPassword)
            }
        }

        if (mode == "register") observeRegister() else observeResetPassword()
    }

    private fun observeRegister() {
        lifecycleScope.launch {
            viewModel.registerState.collect { state ->
                when (state) {
                    is Resource.Loading -> binding.progressBar.visibility = View.VISIBLE
                    is Resource.Success -> {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(this@VerifyOtpActivity, state.data.message, Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@VerifyOtpActivity, LoginActivity::class.java))
                        finishAffinity()
                    }
                    is Resource.Error -> {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(this@VerifyOtpActivity, state.message, Toast.LENGTH_LONG).show()
                    }
                    null -> Unit
                }
            }
        }
    }

    private fun observeResetPassword() {
        lifecycleScope.launch {
            viewModel.forgotPasswordState.collect { state ->
                when (state) {
                    is Resource.Loading -> binding.progressBar.visibility = View.VISIBLE
                    is Resource.Success -> {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(this@VerifyOtpActivity, state.data.message, Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@VerifyOtpActivity, LoginActivity::class.java))
                        finishAffinity()
                    }
                    is Resource.Error -> {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(this@VerifyOtpActivity, state.message, Toast.LENGTH_LONG).show()
                    }
                    null -> Unit
                }
            }
        }
    }
}