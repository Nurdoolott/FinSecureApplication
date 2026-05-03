package com.example.finsecureapp.data.remote.dto

data class StartRegisterRequest(
    val fullName: String,
    val password: String,
    val email: String?,
    val firebaseToken: String  // вместо phoneNumber
)