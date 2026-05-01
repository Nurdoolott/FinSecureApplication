package com.example.finsecureapp.data.remote.api

import com.example.finsecureapp.data.remote.dto.UserProfileResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header

interface UserApi {

    @GET("api/users/me")
    suspend fun getMe(
        @Header("Authorization") token: String
    ): Response<UserProfileResponse>
}