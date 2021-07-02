package ru.netology.nmedia.viewmodel

import androidx.lifecycle.ViewModel
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryInMemoryImpl

class PostViewModel : ViewModel() {

    private val repository: PostRepository = PostRepositoryInMemoryImpl()
    val data get() = repository.data
    fun onLikeButtonClick() = repository.onLikeButtonClick()
    fun onShareButtonClick() = repository.onShareButtonClick()
}