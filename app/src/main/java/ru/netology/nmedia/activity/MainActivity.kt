package ru.netology.nmedia


import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import ru.netology.nmedia.adapter.PostsAdapter
import ru.netology.nmedia.databinding.ActivityMainBinding
import ru.netology.nmedia.util.AndroidUtils
import ru.netology.nmedia.viewmodel.PostViewModel


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val viewModel: PostViewModel by viewModels()
        val adapter = PostsAdapter(viewModel)

        binding.list.adapter = adapter
        binding.group.visibility = View.GONE

        viewModel.data.observe(this) { posts ->
            adapter.submitList(posts)
//            { binding.list.smoothScrollToPosition(0) } функция скролинга с нулевой позиции
        }

        viewModel.edited.observe(this) { post ->
            if (post.id == 0L) return@observe
            binding.group.visibility = View.VISIBLE
            with(binding.content){
                requestFocus()
                setText(post.content)
            }
            binding.editPost.setText(post.content)
        }

        binding.save.setOnClickListener {
            with(binding.content){
                if(text.isNullOrBlank()){
                    Toast.makeText(
                        this@MainActivity,
                        "Context can't be empty!",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }
                viewModel.changeContent(text.toString())
                viewModel.save()

                setText("")
                clearFocus()
                AndroidUtils.hideKeyboard(this)
                binding.group.visibility = View.GONE
            }
        }

        binding.cancelButton.setOnClickListener {
            with(binding.content){
                viewModel.cancel()

                setText("")
                clearFocus()
                AndroidUtils.hideKeyboard(this)
                binding.group.visibility = View.GONE
            }

        }


    }
}