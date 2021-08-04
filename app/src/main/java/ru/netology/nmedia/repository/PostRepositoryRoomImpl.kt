package ru.netology.nmedia.repository

import androidx.lifecycle.Transformations
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.entity.PostEntity

class PostRepositoryRoomImpl (private val dao: PostDao,
) : PostRepository {

    override val data = Transformations.map(dao.getAll()) { list ->
        list.map {
            Post(it.id, it.author, it.content, it.published, it.likedByMe, it.likes, it.shares,
                it.viewed,it.video)
        }
    }

    init {
        postsDefault.forEach { dao.insert(PostEntity.fromDto(it))}

    }


    override fun onLikeButtonClick(id: Long) {
        dao.onLikeButtonClick(id)
    }

    override fun onSaveButtonClick(post: Post) {
        dao.onSaveButtonClick(PostEntity.fromDto(post))
    }

    override fun onRemoveClick(id: Long) {
        dao.onRemoveClick(id)
    }

    override fun onShareButtonClick(id: Long) {
        dao.onShareButtonClick(id)
    }
}
val postsDefault = listOf(
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
        shares = 26,
        viewed = 500,
        video = null
    ), Post(
        id = 2,
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
        shares = 22487576,
        viewed = 500,
        video = null
    ))