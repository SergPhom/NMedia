//package ru.netology.nmedia.repository
//
//import android.util.Log
//import androidx.lifecycle.MutableLiveData
//import ru.netology.nmedia.dao.PostDao
//import ru.netology.nmedia.dto.Post
//
//class PostRepositorySQLiteImpl(
//    private val dao: PostDao
//) : PostRepository {
//    private var posts = emptyList<Post>()
//    override val data = MutableLiveData(posts)
//
//    init {
//        dao.default()
//        posts = dao.getAll()
//        data.value = posts
//    }
//
//    override fun onSaveButtonClick(post: Post) {
//        val id = post.id
//        val saved = dao.onSaveButtonClick(post)
//        posts = if (id == 0L) {
//            listOf(saved) + posts
//        } else {
//            posts.map {
//                if (it.id != id) it else saved
//            }
//        }
//        data.value = posts
//    }
//
//    override fun onLikeButtonClick(id: Long) {
//        dao.onLikeButtonClick(id)
//        posts = posts.map {
//            if (it.id != id) it else it.copy(
//                likedByMe = !it.likedByMe,
//                likes = if (it.likedByMe) it.likes - 1 else it.likes + 1
//            )
//        }
//        data.value = posts
//    }
//
//    override fun onRemoveClick(id: Long) {
//        dao.onRemoveClick(id)
//        posts = posts.filter { it.id != id }
//        data.value = posts
//    }
//
//    override fun onShareButtonClick(id: Long) {
//        dao.onShareButtonClick(id)
//        posts = checkNotNull(data.value).map { if (it.id != id) it
//        else it.copy( shares = it.shares+1) }
//        data.value = posts
//    }
//}
//
