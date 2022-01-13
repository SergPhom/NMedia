package ru.netology.nmedia.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nmedia.BuildConfig
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.CardAdBinding
import ru.netology.nmedia.databinding.CardDayBinding
import ru.netology.nmedia.databinding.CardPostBinding
import ru.netology.nmedia.dto.Ad
import ru.netology.nmedia.dto.DateHeader
import ru.netology.nmedia.dto.FeedItem
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.enumeration.AttachmentType
import ru.netology.nmedia.view.load
import ru.netology.nmedia.view.loadCircleCrop

interface Callback {
    fun onLiked(post: Post){}
    fun onShared(post: Post){}
    fun onRemove(post: Post){}
    fun onEdit(post: Post){}
    fun onPlay(post: Post){}
    fun onSingleView(post: Post){}
    fun onSingleViewImageOnly(post: Post){}
    fun onSavingRetry(post: Post){}
}

class PostsAdapter(
    private val callback: Callback
) : PagingDataAdapter<FeedItem, RecyclerView.ViewHolder>(diffCallback) {

    override fun getItemViewType(position: Int): Int =
        when(getItem(position)){
            is Ad -> R.layout.card_ad
            is Post -> R.layout.card_post
            is DateHeader -> R.layout.card_day
            null -> error("unknown view type")
        }
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)){
            is Ad -> (holder as AdViewHolder)?.bind(item)
            is Post -> (holder as PostViewHolder)?.bind(item)
            is DateHeader ->(holder as DateViewHolder)?.bind(item)
            null -> error("unknown item type")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when(viewType){
            R.layout.card_ad -> {
                val binding = CardAdBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                return AdViewHolder(binding, callback)
            }
            R.layout.card_post -> {
                val binding = CardPostBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false)
                return PostViewHolder(binding, callback)
            }
            R.layout.card_day -> {
                val binding = CardDayBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false)
                return DateViewHolder(binding, callback)
            }
            else -> error("unknown view type: $viewType")
        }
    }

    companion object {
        val diffCallback = object : DiffUtil.ItemCallback<FeedItem>() {
            override fun areItemsTheSame(oldItem: FeedItem, newItem: FeedItem): Boolean {
                if (oldItem::class != newItem::class){
                    return false
                }
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: FeedItem, newItem: FeedItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}

class DateViewHolder(
    private val binding: CardDayBinding,
    private val callback: Callback
) : RecyclerView.ViewHolder(binding.root){

    fun bind(date: DateHeader){
        binding.apply {
            day.text = date.date
        }
    }
}

class AdViewHolder(
    private val binding: CardAdBinding,
    private val callback: Callback
) : RecyclerView.ViewHolder(binding.root){

    fun bind(ad: Ad){
        binding.apply {
            image.load(url = "${BuildConfig.BASE_URL}/media/${ad.image}")
        }
    }
}

class PostViewHolder(
    private val binding: CardPostBinding,
    private val callback: Callback
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(post: Post) {
        binding.apply {
            avatar.loadCircleCrop("${BuildConfig.BASE_URL}/avatars/${post.authorAvatar}")
            author.text = "${post.author}  ${post.id}"
            published.text = post.published.toString()
            content.text = post.content
            shares.text = post.count(post.shares)
            viewes.text = "${post.viewes}"

            likes.text = "${post.likes}"
            likes.setIconResource(
                if (post.likedByMe) R.drawable.ic_liked_24 else R.drawable.ic_likes_24
            )
            likes.isChecked = post.likedByMe
            likes.setIconTintResource(R.color.like_button_tint)

            //******************************************************************Listeners
            likes.setOnClickListener {
                callback.onLiked(post)
            }

            shares.setOnClickListener {
                callback.onShared(post)
                shares.isChecked = false
            }

            menu.visibility = if (post.ownedByMe) View.VISIBLE else View.INVISIBLE

            menu.setOnClickListener {
                PopupMenu(it.context, it).apply{
                    inflate(R.menu.menu_post)
                    menu.setGroupVisible(R.id.owned, post.ownedByMe)
                    setOnMenuItemClickListener { item ->
                        when(item.itemId){
                            R.id.remove -> {
                                callback.onRemove(post)
                                true
                            }
                            R.id.edit -> {
                                callback.onEdit(post)
                                true
                            }
                            else -> false
                        }
                    }
                }.show()
            }

            retrySaving.setOnClickListener {
                callback.onSavingRetry(post)
            }

            video.setOnClickListener {
                callback.onPlay(post)
            }

            videoPlay.setOnClickListener {
                callback.onPlay(post)
            }
            author.setOnClickListener{ callback.onSingleView(post)}
            avatar.setOnClickListener{ callback.onSingleView(post)}
            content.setOnClickListener{ callback.onSingleView(post)}
            published.setOnClickListener{ callback.onSingleView(post)}
            imageAttachment.setOnClickListener { callback.onSingleViewImageOnly(post) }
              //******************************************************************Options
            if (!post.saved){
                binding.buttonGroup.visibility = View.GONE
                binding.retrySaving.visibility = View.VISIBLE
            }

            if (post.attachment != null) {
                when (post.attachment.type){
                    AttachmentType.VIDEO -> videoGroup.visibility = View.VISIBLE
                    AttachmentType.IMAGE ->{
                        imageAttachment.visibility = View.VISIBLE
                        imageAttachment.load("${BuildConfig.BASE_URL}/media/${post.attachment.url}")
                    }
                }
            }
        }
    }
}

