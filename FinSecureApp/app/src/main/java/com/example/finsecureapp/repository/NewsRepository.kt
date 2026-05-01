package com.example.finsecureapp.data.repository

import com.example.finsecureapp.data.remote.dto.ArticleDto
import com.example.finsecureapp.data.remote.retrofit.RetrofitInstance
import com.example.finsecureapp.utils.ErrorUtils
import com.example.finsecureapp.utils.Resource

class NewsRepository {

    suspend fun getTopNews(): Resource<List<ArticleDto>> {
        return try {
            val response = RetrofitInstance.newsApi.getTopNews()

            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!.articles)
            } else {
                Resource.Error(response.errorBody()?.string() ?: "Failed to load news")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unknown error")
        }
    }

    suspend fun searchNews(query: String): Resource<List<ArticleDto>> {
        return try {
            val response = RetrofitInstance.newsApi.searchNews(query)

            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!.articles)
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