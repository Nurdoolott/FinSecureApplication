package com.example.finsecureapp.data.remote.api

import com.example.finsecureapp.data.remote.dto.NewsResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApi {

    @GET("api/news/top")
    suspend fun getTopNews(): Response<NewsResponse>

    @GET("api/news/search")
    suspend fun searchNews(
        @Query("q") query: String
    ): Response<NewsResponse>
}