package ru.netology.nmedia.viewmodel

import android.app.Application
import android.content.Context
import android.widget.Toast
import androidx.lifecycle.*
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_INDEFINITE
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_LONG
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import okhttp3.internal.wait
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
    viewed = 0L,
    saved = false,
    attachment = null
)

class PostViewModel(application: Application) : AndroidViewModel(application){

    private val repository: PostRepository =
        PostRepositoryImpl(AppDb.getInstance(context = application).postDao())

    val data: LiveData<FeedModel> = repository.data.map(::FeedModel)
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

//    fun fillPosts() {
//        _data.postValue(FeedModel(loading = true))
//        repository.fillAsync(object : PostRepository.Callback<Post> {
//            override fun onSuccess(post: Post) {
//                _data.postValue(_data.value?.posts
//                    ?.let { FeedModel(posts = it.plus(post)) })
//            }
//
//            override fun onError(msg: String) {
//                Toast.makeText(getApplication(), msg as CharSequence, Toast.LENGTH_LONG)
//                _data.postValue(FeedModel(error = true))
//            }
//        })
//    }
}