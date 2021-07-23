package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryInFileImpl

private val empty = Post(
    id = 0,
    content = "",
    author = "",
    likedByMe = false,
    published = "",
    shared = 0L,
    likes = 0L,
    viewed = 0L
)

class PostViewModel(application: Application) : AndroidViewModel(application){

    private val repository: PostRepository = PostRepositoryInFileImpl(application)
    val data get() = repository.data
    val edited = MutableLiveData(empty)

    fun onLiked(post: Post) {
        repository.onLikeButtonClick(post.id)
    }

    fun onShared(post: Post) {
        repository.onShareButtonClick(post.id)
    }

    fun onRemove(post: Post) {
        repository.onRevomeClick(post.id)
    }

    fun onEdit(post: Post){
        edited.value = post
    }

    fun save() {
        edited.value?.let {
            repository.onSaveButtonClick(it)
        }
        edited.value = empty
    }

    fun cancel(){
        edited.value = empty
    }

    fun changeContent(content: String) {
        val text = content.trim()
        if (edited.value?.content == text) {
            return
        }
        edited.value = edited.value?.copy(content = text)
    }
}