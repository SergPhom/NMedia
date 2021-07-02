package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.dto.Post

class PostRepositoryInMemoryImpl : PostRepository {

    override val data: MutableLiveData<Post>
    init {
        val post = Post(
            id = 1,
            author = "Нетология. Университет интернет-профессий будущего",
            content = "Привет, это новая Нетология! Когда-то Нетология начиналась с интенсивов по онлайн-маркетингу. Затем появились курсы по дизайну, разработке, аналитике и управлению. Мы растём сами и помогаем расти студентам: от новичков до уверенных профессионалов. Но самое важное остаётся с нами: мы верим, что в каждом уже есть сила, которая заставляет хотеть больше, целиться выше, бежать быстрее. Наша миссия — помочь встать на путь роста и начать цепочку перемен → http://netolo.gy/fyb",
            published = "21 мая в 18:36",
            likedByMe = false,
            likes = 105,
            shared = 22487576,
            viewed = 500
        )
        data = MutableLiveData(post)
    }

    override fun onLikeButtonClick() {
        val post = checkNotNull(data.value)
        data.value = post.copy(likedByMe = !post.likedByMe, likes = when(post.likedByMe){
                false -> post.likes + 1
                else -> post.likes - 1
            } )

    }

    override fun onShareButtonClick() {
        val post = checkNotNull(data.value)
        data.value = post.copy(shared = post.shared + 1)
    }
}