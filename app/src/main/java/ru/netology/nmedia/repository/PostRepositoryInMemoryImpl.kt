package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.dto.Post

class PostRepositoryInMemoryImpl : PostRepository {
    private var nextID = 1L
    private var posts = listOf(
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
            shared = 26,
            viewed = 500
        ), Post(
            id = nextID++,
            author = "Нетология. Университет интернет-профессий будущего",
            content = "Знаний хватит на всех: на следующей неделе разбираемся с " +
                    "разработкой мобильных приложений, учимся рассказывать истории " +
                    "и составлять PR-стратегию прямо на бесплатных занятиях \uD83D\uDC47",
            published = "18 сентября в 10:12",
            likedByMe = false,
            likes = 155,
            shared = 22456,
            viewed = 550
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
            shared = 22487576,
            viewed = 500
        ))

    override val data = MutableLiveData(posts)

    override fun onLikeButtonClick(id: Long) {
        val posts = checkNotNull(data.value).map { if (it.id != id) it
        else it.copy(likedByMe = !it.likedByMe, likes = when(it.likedByMe){
            false -> it.likes + 1
            else -> it.likes - 1
        }) }
        data.value = posts
    }

    override fun onShareButtonClick(id: Long) {
        val posts = checkNotNull(data.value).map { if (it.id != id) it
        else it.copy( shared = it.shared+1) }
        data.value = posts
    }

    override fun onRevomeClick(id: Long) {
        val posts = posts.filter{it.id != id}
        data.value = posts
    }

    override fun onSaveButtonClick(post: Post) {
        if (post.id == 0L) {
            posts = listOf(post.copy(
                id = nextID++,
                author = "Me",
                likedByMe = false,
                published = "now",
                likes = 0L,
                shared = 0L,
                viewed = 0L
            )) + posts
            data.value = posts
            return
        }
        posts = posts.map{
            if(it.id != post.id) it else it.copy(content = post.content)
        }
        data.value = posts
    }
}