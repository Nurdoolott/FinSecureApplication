package com.example.finsecureapp.data.repository

import com.example.finsecureapp.data.remote.dto.*
import com.example.finsecureapp.data.remote.retrofit.RetrofitInstance
import com.example.finsecureapp.utils.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit
import android.app.Activity

class AuthRepository {

    private val firebaseAuth = FirebaseAuth.getInstance()

    // Отправить OTP через Firebase
    fun sendOtp(
        phoneNumber: String,
        activity: Activity,
        callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    ) {
        val options = PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(callbacks)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    suspend fun checkPhone(phoneNumber: String): Resource<Unit> {
        return try {
            val response = RetrofitInstance.authApi.checkPhone(
                mapOf("phoneNumber" to phoneNumber)
            )
            if (response.isSuccessful) {
                Resource.Success(Unit)
            } else {
                Resource.Error(response.errorBody()?.string() ?: "Phone already registered")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unknown error")
        }
    }

    // Подтвердить OTP код и получить Firebase токен
    suspend fun verifyOtpAndGetToken(
        verificationId: String,
        otpCode: String
    ): Resource<String> {
        return try {
            android.util.Log.d("FIREBASE_DEBUG", "Trying to verify: $verificationId with code: $otpCode")
            val credential = PhoneAuthProvider.getCredential(verificationId, otpCode)
            val result = firebaseAuth.signInWithCredential(credential).await()
            android.util.Log.d("FIREBASE_DEBUG", "Sign in success: ${result.user?.uid}")
            val token = result.user?.getIdToken(false)?.await()?.token
                ?: return Resource.Error("Failed to get Firebase token")
            Resource.Success(token)
        } catch (e: Exception) {
            android.util.Log.e("FIREBASE_DEBUG", "Error: ${e.message}", e)
            Resource.Error(e.message ?: "OTP verification failed")
        }
    }

    // Регистрация — отправляет Firebase токен на бэкенд
    suspend fun startRegister(
        fullName: String,
        password: String,
        email: String?,
        firebaseToken: String
    ): Resource<StartRegisterResponse> {
        return try {
            val response = RetrofitInstance.authApi.startRegister(
                StartRegisterRequest(fullName, password, email, firebaseToken)
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

    // Сброс пароля — отправляет Firebase токен + новый пароль
    suspend fun forgotPassword(
        firebaseToken: String,
        newPassword: String
    ): Resource<ForgotPasswordResponse> {
        return try {
            val response = RetrofitInstance.authApi.forgotPassword(
                ForgotPasswordRequest(firebaseToken, newPassword)
            )
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error(response.errorBody()?.string() ?: "Reset failed")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unknown error")
        }
    }

    suspend fun login(phoneNumber: String, password: String): Resource<LoginResponse> {
        return try {
            val response = RetrofitInstance.authApi.login(LoginRequest(phoneNumber, password))
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error(response.errorBody()?.string() ?: "Login failed")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unknown error")
        }
    }
}