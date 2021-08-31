package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.model.FeedModel
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryImpl
import ru.netology.nmedia.util.SingleLiveEvent

private val empty = Post(
    id = 0,
    content = "",
    author = "",
    likedByMe = false,
    published = "",
    shares = 0L,
    likes = 0L,
    viewed = 0L,
    attachment = null
)

class PostViewModel(application: Application) : AndroidViewModel(application){

    private val repository: PostRepository = PostRepositoryImpl()
    val _data = MutableLiveData(FeedModel())
    val data: LiveData<FeedModel>
        get() = _data
    val edited = MutableLiveData(empty)
    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated
    var draft = ""

    init {
        loadPosts()
    }

    fun loadPosts() {
            _data.postValue(FeedModel(loading = true))
            repository.getAllAsync(object : PostRepository.GetAllCallback {
                override fun onSuccess(posts: List<Post>) {
                    _data.postValue(FeedModel(posts = posts, empty = posts.isEmpty()))
                }

                override fun onError(e: Exception) {
                    _data.postValue(FeedModel(error = true))
                }
            })
    }

    fun save() {
        edited.value?.let {
            repository.savePostAsync(it,
                object : PostRepository.LikeCallback {
                    override fun onSuccess() {
                        loadPosts()
                    }

                    override fun onError(e: Exception) {
                        _data.postValue(FeedModel(error = true))
                    }
            })
            _postCreated.postValue(Unit)
        }
        edited.value = empty
    }
    fun onEdit(post: Post){
        edited.value = post
    }

    fun changeContent(content: String) {
        val text = content.trim()
        if (edited.value?.content == text) {
            return
        }
        edited.value = edited.value?.copy(content = text)
    }

    fun onLiked(post: Post) {
        if (post.likedByMe) {
            repository.unlikeByIdAsync(post.id,
                object : PostRepository.LikeCallback {
                    override fun onSuccess() {
                        loadPosts()
                    }

                    override fun onError(e: Exception) {
                        _data.postValue(FeedModel(error = true))
                    }
                })
        } else{
            repository.likeByIdAsync(post.id,
                object : PostRepository.LikeCallback {
                    override fun onSuccess() {
                        loadPosts()
                    }

                override fun onError(e: Exception) {
                    _data.postValue(FeedModel(error = true))
                }
            })
        }
    }

    fun onRemove(post: Post) {
        repository.removeByIdAsync(post.id,
            object : PostRepository.LikeCallback {
                override fun onSuccess() {
                    loadPosts()
                }

                override fun onError(e: Exception) {
                    _data.postValue(FeedModel(error = true))
                }
            })
    }

    fun onShared(post: Post) {
        repository.sharePostAsync(post.id,
            object : PostRepository.LikeCallback {
                override fun onSuccess() {
                    loadPosts()
                }

                override fun onError(e: Exception) {
                    _data.postValue(FeedModel(error = true))
                }
            })
    }

    fun cancel(){
        edited.value = empty
    }

    fun fillPosts() {
        repository.fillAsync(object : PostRepository.LikeCallback {
            override fun onSuccess() {
                loadPosts()
            }

            override fun onError(e: Exception) {
                _data.postValue(FeedModel(error = true))
            }
        })
    }
}