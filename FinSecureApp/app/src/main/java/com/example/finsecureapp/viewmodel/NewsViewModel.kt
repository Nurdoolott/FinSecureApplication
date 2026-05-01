package com.example.finsecureapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.finsecureapp.data.remote.dto.ArticleDto
import com.example.finsecureapp.data.repository.NewsRepository
import com.example.finsecureapp.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class NewsViewModel(
    private val repository: NewsRepository
) : ViewModel() {

    private val _newsState = MutableStateFlow<Resource<List<ArticleDto>>?>(null)
    val newsState: StateFlow<Resource<List<ArticleDto>>?> = _newsState

    fun loadTopNews() {
        viewModelScope.launch {
            _newsState.value = Resource.Loading
            _newsState.value = repository.getTopNews()
        }
    }

    fun searchNews(query: String) {
        viewModelScope.launch {
            _newsState.value = Resource.Loading
            _newsState.value = repository.searchNews(query)
        }
    }
}

class NewsViewModelFactory(
    private val repository: NewsRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NewsViewModel::class.java)) {
            return NewsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}