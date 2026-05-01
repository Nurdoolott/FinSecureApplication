package com.example.finsecureapp.data.remote.dto

data class StartRegisterRequest(
    val fullName: String,
    val phoneNumber: String,
    val password: String,
    val email: String?
)