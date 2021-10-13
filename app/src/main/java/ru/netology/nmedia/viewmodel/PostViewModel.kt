package ru.netology.nmedia.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.dto.MediaUpload
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.model.FeedModel
import ru.netology.nmedia.model.FeedModelState
import ru.netology.nmedia.model.PhotoModel
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryImpl
import ru.netology.nmedia.util.SingleLiveEvent
import java.io.File
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
private val noPhoto = PhotoModel()

class PostViewModel(application: Application) : AndroidViewModel(application){

    private val repository: PostRepository =
        PostRepositoryImpl(AppDb.getInstance(context = application).postDao())

    val data: LiveData<FeedModel> = repository.data
        .map(::FeedModel)
        .asLiveData(Dispatchers.Default)
        .also { it.value?.posts?.filter { post ->  post.viewed } }

    private val _dataState = MutableLiveData<FeedModelState>()
    val dataState: LiveData<FeedModelState>
        get() = _dataState

    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated

    val edited = MutableLiveData(empty)
    var draft = ""

    val newerCount: LiveData<Int> = data.switchMap {
        repository.getNewerCount(it.posts.firstOrNull()?.id ?: 0L)
            .catch { e -> e.printStackTrace() }
            .asLiveData(Dispatchers.Default)
    }

    private val _photo = MutableLiveData(noPhoto)
    val photo: LiveData<PhotoModel>
        get() = _photo

    init {
        loadPosts()
    }

    fun loadPosts() = viewModelScope.launch {
        try {
            _dataState.value = FeedModelState(loading = true)
            repository.getAll()
            _dataState.value = FeedModelState()
        } catch (e: Exception) {
            _dataState.value = FeedModelState( msg = "Loading error")
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
            _dataState.value = FeedModelState( msg = "Refreshing error")
        }
    }

    fun save() {
        edited.value?.let {
            _postCreated.postValue(Unit)
            viewModelScope.launch {
                try {
                    when(_photo.value) {
                        noPhoto -> repository.savePost(it)
                        else -> _photo.value?.file?.let { file ->
                            repository.savePostWithAttachment(it, MediaUpload(file))
                        }
                    }
                    _dataState.value = FeedModelState()
                } catch (e: Exception) {
                    _dataState.value = FeedModelState( msg = "Saving error")
                }
            }
        }
        edited.value = empty
        _photo.value = noPhoto
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
                   _dataState.value = FeedModelState( msg = "disliking error ")
               }
           }
        } else{
            viewModelScope.launch {
                try {
                    repository.likeById(post.id)
                    _dataState.value = FeedModelState()
                } catch (e: Exception) {
                    _dataState.value = FeedModelState( msg = "Liking error ")
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
            _dataState.value = FeedModelState( msg = "Remove error ")
        }
    }

}

    fun onShared(post: Post) {
        viewModelScope.launch {
            try {
                repository.sharePost(post.id)
                _dataState.value = FeedModelState()
            } catch (e: Exception) {
                _dataState.value = FeedModelState( msg = "Share error ")
            }
        }
    }

    fun changePhoto(uri: Uri?, file: File?) {
        _photo.value = PhotoModel(uri, file)
    }

    fun cancel(){
        edited.value = empty
    }
}