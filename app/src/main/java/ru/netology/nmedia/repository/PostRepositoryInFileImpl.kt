package ru.netology.nmedia.repository

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ru.netology.nmedia.dto.Post

class PostRepositoryInFileImpl (private val context: Context): PostRepository {
    private var nextID = 1L
    private val gson = Gson()
    private val type = TypeToken.getParameterized(List::class.java, Post::class.java).type
    private val filename = "posts.json"
    private var posts = emptyList<Post>()

    val postsDefault = listOf(
        Post(
            id = nextID++,
            author = "Нетология. Университет интернет-профессий будущего",
            content = "Привет, это новая Нетология! Когда-то Нетология начиналась " +
                    "с интенсивов по онлайн-маркетингу. Затем появились курсы по дизайну, " +
                    "разработке, аналитике и управлению. Мы растём сами и помогаем расти " +
                    "студентам: от новичков до уверенных профессионалов. Но самое важное " +
                    "остаётся с нами: мы верим, что в каждом уже есть сила, которая " +
                    "заставляет хотеть больше, целиться выше, бежать быстрее. Наша миссия — " +
                    "помочь встать на путь роста и начать цепочку перемен → http://netolo.gy/fyb",
            published = "21 мая в 18:36",
            likedByMe = false,
            likes = 105,
            shares = 26,
            viewed = 500,
            video = null
        ), Post(
            id = nextID++,
            author = "Нетология. Университет интернет-профессий будущего",
            content = "Знаний хватит на всех: на следующей неделе разбираемся с " +
                    "разработкой мобильных приложений, учимся рассказывать истории " +
                    "и составлять PR-стратегию прямо на бесплатных занятиях \uD83D\uDC47",
            published = "18 сентября в 10:12",
            likedByMe = false,
            likes = 155,
            shares = 22456,
            viewed = 550,
            video = "https://www.youtube.com/watch?v=WhWc3b3KhnY"
        ),
        Post(
            id = nextID++,
            author = "Нетология. Университет интернет-профессий будущего",
            content = "Привет, это новая Нетология! Когда-то Нетология начиналась " +
                    "с интенсивов по онлайн-маркетингу. Затем появились курсы по дизайну, " +
                    "разработке, аналитике и управлению. Мы растём сами и помогаем расти " +
                    "студентам: от новичков до уверенных профессионалов. Но самое важное " +
                    "остаётся с нами: мы верим, что в каждом уже есть сила, которая " +
                    "заставляет хотеть больше, целиться выше, бежать быстрее. Наша миссия — " +
                    "помочь встать на путь роста и начать цепочку перемен → http://netolo.gy/fyb",
            published = "21 мая в 18:36",
            likedByMe = false,
            likes = 105,
            shares = 22487576,
            viewed = 500,
            video = null
        ))

    override val data = MutableLiveData(posts)

    init {
        context.openFileOutput(filename, Context.MODE_PRIVATE).bufferedWriter().use {
            it.write(gson.toJson(postsDefault))
        }

        val file = context.filesDir.resolve(filename)
        if (file.exists()) {
            context.openFileInput(filename).bufferedReader().use {
                posts = gson.fromJson(it, type)
                data.value = posts
            }
        } else {
            sync()
        }
    }

    override fun onLikeButtonClick(id: Long) {
        posts = posts.map {
            if (it.id != id) it else it.copy(
                likedByMe = !it.likedByMe,
                likes = if (it.likedByMe) it.likes - 1 else it.likes + 1
            )
        }
        data.value = posts
        sync()
    }

    override fun onShareButtonClick(id: Long) {
        posts = checkNotNull(data.value).map { if (it.id != id) it
        else it.copy( shares = it.shares+1) }
        data.value = posts
    }

    override fun onRemoveClick(id: Long) {
        posts = posts.filter { it.id != id }
        data.value = posts
        sync()
    }

    override fun onSaveButtonClick(post: Post) {
        if (post.id == 0L) {
            posts = listOf(post.copy(
                id = nextID++,
                author = "Me",
                likedByMe = false,
                published = "now",
                likes = 0L,
                shares = 0L,
                viewed = 0L,
                video = null
            )) + posts
        }else {
            posts = posts.map {
                if (it.id != post.id) it else it.copy(content = post.content)
            }
        }
        data.value = posts
        sync()
    }

    private fun sync() {
        context.openFileOutput(filename, Context.MODE_PRIVATE).bufferedWriter().use {
            it.write(gson.toJson(posts))
        }
//        Log.i("TAG", "${file.absolutePath}")
    }
}