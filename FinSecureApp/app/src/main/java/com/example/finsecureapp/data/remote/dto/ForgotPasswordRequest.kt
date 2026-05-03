package com.example.finsecureapp.data.remote.dto

data class ForgotPasswordRequest(
    val firebaseToken: String,  // вместо phoneNumber
    val newPassword: String
)