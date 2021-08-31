package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import ru.netology.nmedia.dto.Post

interface PostRepository {
    fun fillAsync(callback: LikeCallback)

    fun getAllAsync(callback: GetAllCallback)

    interface GetAllCallback {
        fun onSuccess(posts: List<Post>) {}
        fun onError(e: Exception) {}
    }

    fun likeByIdAsync(id: Long, callback: LikeCallback)
    interface LikeCallback {
        fun onSuccess() {}
        fun onError(e: Exception) {}
    }

    fun unlikeByIdAsync(id: Long, callback: LikeCallback)

    fun removeByIdAsync(id: Long, callback: LikeCallback)

    fun savePostAsync(post: Post, callback: LikeCallback)

    fun sharePostAsync(id: Long, callback: LikeCallback)
}