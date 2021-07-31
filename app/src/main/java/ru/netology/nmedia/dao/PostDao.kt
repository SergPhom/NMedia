package ru.netology.nmedia.dao

import ru.netology.nmedia.dto.Post
import java.io.Closeable

interface PostDao {
    fun getAll(): List<Post>
    fun onLikeButtonClick(id: Long)
    fun onShareButtonClick(id: Long)
    fun onRemoveClick(id: Long)
    fun onSaveButtonClick(post: Post):Post
    fun default()
}
