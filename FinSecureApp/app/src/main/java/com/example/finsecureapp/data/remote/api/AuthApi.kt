package com.example.finsecureapp.data.remote.api

import com.example.finsecureapp.data.remote.dto.ForgotPasswordRequest
import com.example.finsecureapp.data.remote.dto.ForgotPasswordResponse
import com.example.finsecureapp.data.remote.dto.LoginRequest
import com.example.finsecureapp.data.remote.dto.LoginResponse
import com.example.finsecureapp.data.remote.dto.ResetPasswordRequest
import com.example.finsecureapp.data.remote.dto.ResetPasswordResponse
import com.example.finsecureapp.data.remote.dto.StartRegisterRequest
import com.example.finsecureapp.data.remote.dto.StartRegisterResponse
import com.example.finsecureapp.data.remote.dto.VerifyRegisterRequest
import com.example.finsecureapp.data.remote.dto.VerifyRegisterResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {

    @POST("api/auth/start-register")
    suspend fun startRegister(
        @Body request: StartRegisterRequest
    ): Response<StartRegisterResponse>

    @POST("api/auth/verify-register")
    suspend fun verifyRegister(
        @Body request: VerifyRegisterRequest
    ): Response<VerifyRegisterResponse>

    @POST("api/auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>

    @POST("api/auth/forgot-password")
    suspend fun forgotPassword(
        @Body request: ForgotPasswordRequest
    ): Response<ForgotPasswordResponse>

    @POST("api/auth/reset-password")
    suspend fun resetPassword(
        @Body request: ResetPasswordRequest
    ): Response<ResetPasswordResponse>
}