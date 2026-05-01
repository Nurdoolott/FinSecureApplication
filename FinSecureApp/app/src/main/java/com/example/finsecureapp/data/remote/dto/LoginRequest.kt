package com.example.finsecureapp.data.remote.dto

data class LoginRequest(
    val phoneNumber: String,
    val password: String
)