package ru.netology.nmedia.repository

import ru.netology.nmedia.dto.Post

interface PostRepository {
    fun fillAsync(callback: Callback<Post>)

    interface GetAllCallback<T> {
        fun onSuccess(posts: T) {}
        fun onError(msg: String) {}
    }

    fun getAllAsync(callback: GetAllCallback<List<Post>>)

    interface Callback<T> {
        fun onSuccess(post: T) {}
        fun onError(s: String) {}
    }

    fun likeByIdAsync(id: Long, callback: Callback<Post>)

    fun dislikeByIdAsync(id: Long, callback: Callback<Post>)

    fun savePostAsync(post: Post, callback: Callback<Post>)

    fun sharePostAsync(id: Long, callback: Callback<Post>)

    interface CallbackUnit<T>{
        fun onSuccess(id: Long){}
        fun onError(msg: String){}
    }

    fun removeByIdAsync(id: Long, callback: CallbackUnit<Long>)


}