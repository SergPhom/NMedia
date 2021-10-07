package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import ru.netology.nmedia.dto.Post

interface PostRepository {
    val data: LiveData<List<Post>>

    suspend fun fill()
//            **************

    suspend fun getAll()
//             ***************

    suspend fun likeById(id: Long)

    suspend fun dislikeById(id: Long)

    suspend fun savePost(post: Post)

    suspend fun sharePost(id: Long)
//            ***************

    suspend fun removeById(id: Long)

}