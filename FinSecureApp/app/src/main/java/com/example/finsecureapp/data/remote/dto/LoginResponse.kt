package com.example.finsecureapp.data.remote.dto

data class LoginResponse(
    val message: String,
    val token: String,
    val user: UserDto
)