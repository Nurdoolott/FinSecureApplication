package com.example.finsecureapp.ui.auth.forgot

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.finsecureapp.data.local.datastore.TokenManager
import com.example.finsecureapp.data.repository.AuthRepository
import com.example.finsecureapp.databinding.ActivityForgotPasswordBinding
import com.example.finsecureapp.ui.auth.verify.VerifyOtpActivity
import com.example.finsecureapp.utils.Resource
import com.example.finsecureapp.viewmodel.AuthViewModel
import com.example.finsecureapp.viewmodel.AuthViewModelFactory
import kotlinx.coroutines.launch

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityForgotPasswordBinding
    private lateinit var viewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(
            this,
            AuthViewModelFactory(AuthRepository(), TokenManager(applicationContext))
        )[AuthViewModel::class.java]

        binding.btnSendOtp.setOnClickListener {
            val phoneNumber = binding.etPhoneNumber.text.toString().trim()

            if (phoneNumber.isEmpty()) {
                Toast.makeText(this, "Enter phone number", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.pendingPhone = phoneNumber
            viewModel.sendOtp(phoneNumber, this)
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
                        val intent = Intent(this@ForgotPasswordActivity, VerifyOtpActivity::class.java)
                        intent.putExtra("mode", "forgot")
                        intent.putExtra("verificationId", viewModel.verificationId) // добавить
                        startActivity(intent)
                    }
                    is Resource.Error -> {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(this@ForgotPasswordActivity, state.message, Toast.LENGTH_LONG).show()
                    }
                    null -> Unit
                }
            }
        }
    }
}