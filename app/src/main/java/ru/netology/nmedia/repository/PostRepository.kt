package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import ru.netology.nmedia.dto.Post

interface PostRepository {
    val data: LiveData<List<Post>>
    fun onLikeButtonClick(id: Long)
    fun onShareButtonClick(id: Long)
    fun onRemoveClick(id: Long)
    fun onSaveButtonClick(post: Post)
    fun fill()
}