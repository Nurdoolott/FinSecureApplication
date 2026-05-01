package com.example.finsecureapp.data.repository

import com.example.finsecureapp.data.remote.dto.BalanceResponse
import com.example.finsecureapp.data.remote.retrofit.RetrofitInstance
import com.example.finsecureapp.utils.ErrorUtils
import com.example.finsecureapp.utils.Resource

class AccountRepository {

    suspend fun getBalance(token: String): Resource<BalanceResponse> {
        return try {
            val response = RetrofitInstance.accountApi.getBalance("Bearer $token")

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