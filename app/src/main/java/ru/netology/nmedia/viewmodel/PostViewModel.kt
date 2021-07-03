package ru.netology.nmedia.viewmodel

import androidx.lifecycle.ViewModel
import ru.netology.nmedia.adapter.Callback
import ru.netology.nmedia.adapter.likeListener
import ru.netology.nmedia.adapter.sharedListener
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryInMemoryImpl


class PostViewModel : ViewModel(), Callback {

    private val repository: PostRepository = PostRepositoryInMemoryImpl()
    val data get() = repository.data
//    fun onLikeButtonClickListener(post: Post) = repository.onLikeButtonClick(post.id)
    override fun onLiked(post: Post) {
        repository.onLikeButtonClick(post.id)
    }

    override fun onShared(post: Post) {
        repository.onShareButtonClick(post.id)
    }

}