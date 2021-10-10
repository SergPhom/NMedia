package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.model.FeedModel
import ru.netology.nmedia.model.FeedModelState
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryImpl
import ru.netology.nmedia.util.SingleLiveEvent
import java.lang.Exception

private val empty = Post(
    id = 0,
    content = "",
    author = "",
    authorAvatar = "",
    likedByMe = false,
    published = 12345L,
    shares = 0L,
    likes = 0L,
    viewes = 0L,
    viewed = true,
    saved = false,
    attachment = null
)

class PostViewModel(application: Application) : AndroidViewModel(application){

    private val repository: PostRepository =
        PostRepositoryImpl(AppDb.getInstance(context = application).postDao())

    val data: LiveData<FeedModel> = repository.data
        .map(::FeedModel)
        .asLiveData(Dispatchers.Default)
        .also { it.value?.posts?.filter { post ->  post.viewed } }

    val newerCount: LiveData<Int> = data.switchMap {
        repository.getNewerCount(it.posts.firstOrNull()?.id ?: 0L)
            .asLiveData(Dispatchers.Default)
    }

    private val _dataState = MutableLiveData<FeedModelState>()
    val dataState: LiveData<FeedModelState>
        get() = _dataState
    val edited = MutableLiveData(empty)
    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated
    var draft = ""

    init {
        loadPosts()
    }

    fun loadPosts() = viewModelScope.launch {
        try {
            _dataState.value = FeedModelState(loading = true)
            repository.getAll()
            _dataState.value = FeedModelState()
        } catch (e: Exception) {
            _dataState.value = FeedModelState( msg = "Loading error $e")
        }
    }

    fun markNewerPostsViewed()= viewModelScope.launch {
        try {
            repository.newerPostsViewed()
        } catch (e: Exception) {
            println("PW $e")
            return@launch
        }
    }

    fun refreshPosts() = viewModelScope.launch {
        try {
            _dataState.value = FeedModelState(refreshing = true)
            repository.getAll()
            _dataState.value = FeedModelState()
        } catch (e: Exception) {
            _dataState.value = FeedModelState( msg = "Refreshing error $e")
        }
    }

    fun save() {
        edited.value?.let {
            _postCreated.postValue(Unit)
            viewModelScope.launch {
                try {
                    repository.savePost(it)
                    _dataState.value = FeedModelState()
                } catch (e: Exception) {
                    _dataState.value = FeedModelState( msg = "Saving error $e")
                }
            }
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
//                                                                    ****LIKES BLOCK****
    fun onLiked(post: Post) {
        if (post.likedByMe) {
           viewModelScope.launch {
               try {
                   repository.dislikeById(post.id)
                   _dataState.value = FeedModelState()
               } catch (e: Exception) {
                   _dataState.value = FeedModelState( msg = "disliking error $e")
               }
           }
        } else{
            viewModelScope.launch {
                try {
                    repository.likeById(post.id)
                    _dataState.value = FeedModelState()
                } catch (e: Exception) {
                    _dataState.value = FeedModelState( msg = "Liking error $e")
                }
            }
        }
    }
//                                                                   ****REMOVE BLOCK****
    fun onRemove(post: Post) {
    viewModelScope.launch {
        try {
            repository.removeById(post.id)
            _dataState.value = FeedModelState()
        } catch (e: Exception) {
            _dataState.value = FeedModelState( msg = "Remove error $e")
        }
    }

}

    fun onShared(post: Post) {
        viewModelScope.launch {
            try {
                repository.sharePost(post.id)
                _dataState.value = FeedModelState()
            } catch (e: Exception) {
                _dataState.value = FeedModelState( msg = "Share error $e")
            }
        }
    }

    fun cancel(){
        edited.value = empty
    }
}