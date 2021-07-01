package ru.netology.nmedia

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
//import android.util.Log
import ru.netology.nmedia.databinding.ActivityMainBinding

data class Post(
    val id: Long,
    val author: String,
    val content: String,
    val published: String,
    var likedByMe: Boolean = false,
    var likes: Long,
    var shared: Int,
    val viewed: Long
)

fun count(value: Int): String = when(value){
    in 1000..9999 -> "${value/1000}" +
            "${if ((value%1000)/100 == 0) "" else { "." + (value%1000)/100} + "K"}"
    in 10000..999999 ->  "${value/1000}K"
    in 1000000..Int.MAX_VALUE -> "${value/1000000}" +
            "${if ((value%1000000)/100000 == 0) "" else { "." + (value%1000000)/100000} + "M"}"
    else -> value.toString()
}

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val post = Post(
            id = 1,
            author = "Нетология. UУниверситет интернет-профессий будущего",
            content = "Привет, это новая Нетология! Когда-то Нетология начиналась с интенсивов по онлайн-маркетингу. Затем появились курсы по дизайну, разработке, аналитике и управлению. Мы растём сами и помогаем расти студентам: от новичков до уверенных профессионалов. Но самое важное остаётся с нами: мы верим, что в каждом уже есть сила, которая заставляет хотеть больше, целиться выше, бежать быстрее. Наша миссия — помочь встать на путь роста и начать цепочку перемен → http://netolo.gy/fyb",
            published = "21 мая в 18:36",
            likedByMe = false,
            likes = 105,
            shared = 22487576,
            viewed = 500
        )

        with(binding){
            avatar.setImageResource(R.drawable.ic_avatar_foreground)
            author.text = post.author
            published.text = post.published
            content.text = post.content
            countLikes.text = post.likes.toString()
            countShared.text = count(post.shared)
            countViewed.text = post.viewed.toString()

            if (post.likedByMe) {
                likes.setImageResource(R.drawable.ic_liked_24)
            }

//            root.setOnClickListener {
//                Log.d("stuff", "stuff")
//            }
//
//            avatar.setOnClickListener {
//                Log.d("stuff", "avatar")
//            }

            likes.setOnClickListener {
//                Log.d("stuff", "like")
                post.likedByMe = !post.likedByMe
                likes.setImageResource(
                    if (post.likedByMe) R.drawable.ic_liked_24 else R.drawable.ic_likes_24
                )
                if (post.likedByMe) post.likes++ else post.likes--
                countLikes.text = post.likes.toString()
            }

            shared.setOnClickListener {
                post.shared++
                countShared.text = count(post.shared)
            }
        }
    }
}