package ru.netology.nmedia.viewmodel

import android.app.Application
import android.content.Context
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_INDEFINITE
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_LONG
import com.google.android.material.snackbar.Snackbar
import okhttp3.internal.wait
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
        repository.getAllAsync(object : PostRepository.GetAllCallback<List<Post>> {
            override fun onSuccess(posts: List<Post>) {
                _data.postValue(FeedModel(posts = posts, empty = posts.isEmpty()))
            }

            override fun onError(msg: String) {
                _data.postValue(FeedModel(error = true, msg = msg))
            }
        })
    }

    fun save() {
        edited.value?.let {
            repository.savePostAsync(it,
                object : PostRepository.Callback<Post> {
                    override fun onSuccess(post: Post) {
                        loadPosts()
                        edited.value = empty
                    }

                    override fun onError(msg: String) {
                        draft = edited.value!!.content                     // черновик поста
                        edited.value = empty
                        _data.postValue(FeedModel(error = true, msg = msg))
                    }
            })
            _postCreated.postValue(Unit)
        }
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
            repository.dislikeByIdAsync(post.id,
                object : PostRepository.Callback<Post> {
                    override fun onSuccess(post: Post) {
                        loadPosts()
                    }

                    override fun onError(msg: String) {
                        _data.postValue(FeedModel(error = true, msg = msg))
                    }
                }
            )
        } else{
            repository.likeByIdAsync(post.id,
                object : PostRepository.Callback<Post> {
                    override fun onSuccess(update: Post) {
                        loadPosts()
                    }
                    override fun onError(msg: String) {
                        _data.postValue(FeedModel(error = true, msg = msg))
                    }
                }
            )
        }
    }
//                                                                   ****REMOVE BLOCK****
    fun onRemove(post: Post) {
        repository.removeByIdAsync(post.id,
            object : PostRepository.CallbackUnit<Unit> {
                override fun onSuccess() {
                    _data.postValue(data.value?.posts?.let { list ->
                        FeedModel(posts = list.filter{
                            it.id != post.id
                        })
                    })
                }

                override fun onError(msg: String) {
                    _data.postValue(FeedModel(error = true, msg = msg))
                }
            })
    }

    fun onShared(post: Post) {
        repository.sharePostAsync(post.id,
            object : PostRepository.Callback<Post>{
                override fun onSuccess(update: Post) {
                    loadPosts()
//                    _data.postValue(data.value?.posts?.let { list->
//                        FeedModel(posts = list.map {
//                            if(it.id == update.id) it.copy(shares = update.shares)
//                            else it
//                        })
//                    })
                }

                override fun onError(msg: String) {
                    _data.postValue(FeedModel(error = true, msg = msg))
                }
            }
        )
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