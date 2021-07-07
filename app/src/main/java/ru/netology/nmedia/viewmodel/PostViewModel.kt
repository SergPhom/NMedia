package ru.netology.nmedia.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.netology.nmedia.adapter.Callback
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryInMemoryImpl

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

class PostViewModel : ViewModel(), Callback {

    private val repository: PostRepository = PostRepositoryInMemoryImpl()
    val data get() = repository.data
    val edited = MutableLiveData(empty)

    fun save() {
        edited.value?.let {
            repository.onSaveButtonClick(it)
        }
        edited.value = empty
    }

    fun cancel(){
        edited.value = empty
    }

    override fun onEdit(post: Post){
        edited.value = post
    }

    fun changeContent(content: String) {
        val text = content.trim()
        if (edited.value?.content == text) {
            return
        }
        edited.value = edited.value?.copy(content = text)
    }

    override fun onLiked(post: Post) {
        repository.onLikeButtonClick(post.id)
    }

    override fun onShared(post: Post) {
        repository.onShareButtonClick(post.id)
    }

    override fun onRemove(post: Post) {
        repository.onRevomeClick(post.id)
    }

}