package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.dto.Post

class PostRepositoryInMemoryImpl : PostRepository {

    override val data: MutableLiveData<List<Post>>
    init {
        val posts = listOf(
            Post(
                id = 1,
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
                id = 2,
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
                id = 3,
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
        data = MutableLiveData(posts)
    }

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
//        val post = checkNotNull(data.value)
//        data.value = post.copy(shared = post.shared + 1)
    }
}