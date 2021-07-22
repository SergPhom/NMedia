package ru.netology.nmedia


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.telecom.Call
import android.view.View
import android.widget.Toast
import androidx.activity.result.launch
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import ru.netology.nmedia.activity.NewPostActivity
import ru.netology.nmedia.activity.NewPostResultContract
import ru.netology.nmedia.adapter.Callback
import ru.netology.nmedia.adapter.PostsAdapter
import ru.netology.nmedia.databinding.ActivityMainBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.viewmodel.PostViewModel


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val viewModel: PostViewModel by viewModels()

        val newPostLauncher = registerForActivityResult(NewPostResultContract()) { result ->
            result ?: return@registerForActivityResult
            viewModel.changeContent(result)
            viewModel.save()
        }

        binding.fab.setOnClickListener {
            newPostLauncher.launch(null)
        }

        val adapter = PostsAdapter(object : Callback {
            override fun onLiked(post: Post) {
                viewModel.onLiked(post)
            }

            override fun onShared(post: Post) {
                viewModel.onShared(post)
                val intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, post.content)
                    type = "text/plain"
                }

                val shareIntent =
                    Intent.createChooser(intent, getString(R.string.chooser_share_post))
                startActivity(shareIntent)
            }

            override fun onRemove(post: Post) {
                viewModel.onRemove(post)
            }

            override fun onEdit(post: Post) {
                viewModel.onEdit(post)
                newPostLauncher.launch( post.content)
//                val intent = Intent(this@MainActivity, NewPostActivity::class.java)
//                    .apply { putExtra(Intent.EXTRA_TEXT, post.content) }
//                startActivity(intent)
            }

            override fun onPlay(post: Post) {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(post.video))
                startActivity(intent)
            }
        })

        binding.list.adapter = adapter


        viewModel.data.observe(this) { posts ->
            adapter.submitList(posts)
//            { binding.list.smoothScrollToPosition(0) } функция скролинга к нулевой позиции
        }

    }
}