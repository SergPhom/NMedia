package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import ru.netology.nmedia.dto.Post

interface PostRepository {
    fun getAll(): List<Post>
    fun likeById(id: Long)
    fun unlikeById(id: Long)
    fun onShareButtonClick(id: Long)
    fun onRemoveClick(id: Long)
    fun onSaveButtonClick(post: Post)
    fun fill()
}