package com.example.finsecureapp.ui.auth.verify

import android.content.Intent
import android.os.Bundle
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
    private lateinit var pendingRegistrationId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVerifyOtpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        pendingRegistrationId = intent.getStringExtra("pendingRegistrationId").orEmpty()

        viewModel = ViewModelProvider(
            this,
            AuthViewModelFactory(
                AuthRepository(),
                TokenManager(applicationContext)
            )
        )[AuthViewModel::class.java]

        binding.btnVerifyOtp.setOnClickListener {
            val otpCode = binding.etOtpCode.text.toString().trim()

            if (otpCode.isEmpty()) {
                Toast.makeText(this, "Enter OTP code", Toast.LENGTH_SHORT).show()
            } else {
                viewModel.verifyRegister(pendingRegistrationId, otpCode)
            }
        }

        observeVerifyOtp()
    }

    private fun observeVerifyOtp() {
        lifecycleScope.launch {
            viewModel.verifyRegisterState.collect { state ->
                when (state) {
                    is Resource.Loading -> {
                        binding.progressBar.visibility = android.view.View.VISIBLE
                    }

                    is Resource.Success -> {
                        binding.progressBar.visibility = android.view.View.GONE
                        Toast.makeText(this@VerifyOtpActivity, state.data.message, Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@VerifyOtpActivity, LoginActivity::class.java))
                        finishAffinity()
                    }

                    is Resource.Error -> {
                        binding.progressBar.visibility = android.view.View.GONE
                        Toast.makeText(this@VerifyOtpActivity, state.message, Toast.LENGTH_LONG).show()
                    }

                    null -> Unit
                }
            }
        }
    }
}