package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import kotlinx.coroutines.flow.Flow
import ru.netology.nmedia.dto.Post

interface PostRepository {
    val data: Flow<List<Post>>
//                 **************              READ
    suspend fun getAll()

    fun getNewerCount(id: Long): Flow<Int>

//             ***************                UPDATE

    suspend fun likeById(id: Long)

    suspend fun dislikeById(id: Long)

    suspend fun savePost(post: Post)

    suspend fun sharePost(id: Long)

    suspend fun newerPostsViewed()
//            ***************               REMOVE

    suspend fun removeById(id: Long)

}