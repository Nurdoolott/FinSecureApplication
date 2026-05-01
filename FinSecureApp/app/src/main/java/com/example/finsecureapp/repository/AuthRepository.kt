package com.example.finsecureapp.data.repository

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
import com.example.finsecureapp.data.remote.retrofit.RetrofitInstance
import com.example.finsecureapp.utils.ErrorUtils
import com.example.finsecureapp.utils.Resource

class AuthRepository {

    suspend fun login(phoneNumber: String, password: String): Resource<LoginResponse> {
        return try {
            val response = RetrofitInstance.authApi.login(
                LoginRequest(phoneNumber, password)
            )

            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error(response.errorBody()?.string() ?: "Login failed")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unknown error")
        }
    }

    suspend fun startRegister(
        fullName: String,
        phoneNumber: String,
        password: String,
        email: String?
    ): Resource<StartRegisterResponse> {
        return try {
            val response = RetrofitInstance.authApi.startRegister(
                StartRegisterRequest(fullName, phoneNumber, password, email)
            )

            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error(response.errorBody()?.string() ?: "Registration failed")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unknown error")
        }
    }

    suspend fun verifyRegister(
        pendingRegistrationId: String,
        otpCode: String
    ): Resource<VerifyRegisterResponse> {
        return try {
            val response = RetrofitInstance.authApi.verifyRegister(
                VerifyRegisterRequest(pendingRegistrationId, otpCode)
            )

            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error(response.errorBody()?.string() ?: "OTP verification failed")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unknown error")
        }
    }

    suspend fun forgotPassword(phoneNumber: String): Resource<ForgotPasswordResponse> {
        return try {
            val response = RetrofitInstance.authApi.forgotPassword(
                ForgotPasswordRequest(phoneNumber)
            )

            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error(response.errorBody()?.string() ?: "Forgot password failed")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unknown error")
        }
    }

    suspend fun resetPassword(
        pendingResetId: String,
        otpCode: String,
        newPassword: String
    ): Resource<ResetPasswordResponse> {
        return try {
            val response = RetrofitInstance.authApi.resetPassword(
                ResetPasswordRequest(pendingResetId, otpCode, newPassword)
            )

            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error(
                    ErrorUtils.extractMessage(response.errorBody()?.string())
                )
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unknown error")
        }
    }
}