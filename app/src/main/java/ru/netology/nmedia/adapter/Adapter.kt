package ru.netology.nmedia.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.CardPostBinding
import ru.netology.nmedia.dto.Post


interface Callback {
    fun onLiked(post: Post)
    fun onShared(post: Post)
}

//typealias likeListener = (post: Post) -> Unit
//typealias sharedListener = (post: Post) -> Unit

class PostsAdapter(
    private val callback: Callback
//    private val likeListener: likeListener,
//    private val sharedListener: sharedListener
) :
    ListAdapter<Post, PostViewHolder>(PostsDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = CardPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding,
            callback
//            likeListener,
//            sharedListener
        )
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

}

class PostViewHolder(
    private val binding: CardPostBinding,
    private val callback: Callback
//    private val likeListener: likeListener,
//    private val sharedListener: sharedListener
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(post: Post) {
        binding.apply {
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
            likes.setOnClickListener {
                callback.onLiked(post)
//                likeListener(post)
            }
            shared.setOnClickListener {
                callback.onShared(post)
//                sharedListener(post)
            }
        }
    }
}

class PostsDiffCallback: DiffUtil.ItemCallback<Post>() {
    override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem == newItem
    }

}