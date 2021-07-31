package ru.netology.nmedia.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.repository.PostRepositorySQLiteImpl
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.repository.PostRepository

private val empty = Post(
    id = 0,
    content = "",
    author = "",
    likedByMe = false,
    published = "",
    shares = 0L,
    likes = 0L,
    viewed = 0L
)

class PostViewModel(application: Application) : AndroidViewModel(application){

    private val repository: PostRepository = PostRepositorySQLiteImpl(
        AppDb.getInstance(application).postDao)
//    private val repository: PostRepository = PostRepositoryInFileImpl(
//        application)
    val data get() = repository.data
    val edited = MutableLiveData(empty)
    var draft = ""

    fun onLiked(post: Post) {
        repository.onLikeButtonClick(post.id)
    }

    fun onShared(post: Post) {
        repository.onShareButtonClick(post.id)
    }

    fun onRemove(post: Post) {
        repository.onRemoveClick(post.id)
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