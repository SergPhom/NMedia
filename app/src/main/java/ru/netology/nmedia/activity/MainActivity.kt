package ru.netology.nmedia


import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import ru.netology.nmedia.databinding.ActivityMainBinding
import ru.netology.nmedia.viewmodel.PostViewModel


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val viewModel: PostViewModel by viewModels()
        viewModel.data.observe(this) { post ->
            with(binding) {
                avatar.setImageResource(R.drawable.ic_avatar_foreground)
                author.text = post.author
                published.text = post.published
                content.text = post.content
                countLikes.text = post.likes.toString()
                countShared.text = post.count(post.shared)
                countViewed.text = post.viewed.toString()
                likes.setImageResource(
                    if (post.likedByMe) R.drawable.ic_liked_24 else R.drawable.ic_likes_24
                )
            }
        }
        binding.likes.setOnClickListener {
            viewModel.onLikeButtonClick()
        }
        binding.shared.setOnClickListener {
            viewModel.onShareButtonClick()
        }

//        val post = Post(
//            id = 1,
//            author = "Нетология. UУниверситет интернет-профессий будущего",
//            content = "Привет, это новая Нетология! Когда-то Нетология начиналась с интенсивов по онлайн-маркетингу. Затем появились курсы по дизайну, разработке, аналитике и управлению. Мы растём сами и помогаем расти студентам: от новичков до уверенных профессионалов. Но самое важное остаётся с нами: мы верим, что в каждом уже есть сила, которая заставляет хотеть больше, целиться выше, бежать быстрее. Наша миссия — помочь встать на путь роста и начать цепочку перемен → http://netolo.gy/fyb",
//            published = "21 мая в 18:36",
//            likedByMe = false,
//            likes = 105,
//            shared = 22487576,
//            viewed = 500
//        )
//
//        with(binding){
//            avatar.setImageResource(R.drawable.ic_avatar_foreground)
//            author.text = post.author
//            published.text = post.published
//            content.text = post.content
//            countLikes.text = post.likes.toString()
//            countShared.text = count(post.shared)
//            countViewed.text = post.viewed.toString()
//
//            if (post.likedByMe) {
//                likes.setImageResource(R.drawable.ic_liked_24)
//            }
//
//            likes.setOnClickListener {
//                post.likedByMe = !post.likedByMe
//                likes.setImageResource(
//                    if (post.likedByMe) R.drawable.ic_liked_24 else R.drawable.ic_likes_24
//                )
//                if (post.likedByMe) post.likes++ else post.likes--
//                countLikes.text = post.likes.toString()
//            }
//
//            shared.setOnClickListener {
//                post.shared++
//                countShared.text = count(post.shared)
//            }
//        }
    }
}