package com.example.finsecureapp.data.repository

import com.example.finsecureapp.data.remote.dto.UserProfileResponse
import com.example.finsecureapp.data.remote.retrofit.RetrofitInstance
import com.example.finsecureapp.utils.Resource

class UserRepository {

    suspend fun getMe(token: String): Resource<UserProfileResponse> {
        return try {
            val response = RetrofitInstance.userApi.getMe("Bearer $token")

            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error("Invalid or expired token")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unknown error")
        }
    }
}