package com.example.finsecureapp.ui.profile

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.finsecureapp.data.local.datastore.TokenManager
import com.example.finsecureapp.databinding.ActivityProfileBinding
import com.example.finsecureapp.ui.auth.login.LoginActivity
import kotlinx.coroutines.launch

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var tokenManager: TokenManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tokenManager = TokenManager(applicationContext)

        val fullName = intent.getStringExtra("fullName").orEmpty()
        val phoneNumber = intent.getStringExtra("phoneNumber").orEmpty()
        val email = intent.getStringExtra("email").orEmpty()

        binding.tvFullName.text = fullName
        binding.tvPhoneNumber.text = phoneNumber
        binding.tvEmail.text = if (email.isBlank()) "Not provided" else email

        binding.btnLogout.setOnClickListener {
            lifecycleScope.launch {
                tokenManager.clearToken()
                startActivity(Intent(this@ProfileActivity, LoginActivity::class.java))
                finishAffinity()
            }
        }
    }
}