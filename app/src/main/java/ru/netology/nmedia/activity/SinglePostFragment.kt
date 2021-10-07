package ru.netology.nmedia.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.PopupMenu
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import ru.netology.nmedia.BuildConfig
import ru.netology.nmedia.FeedFragment
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentSinglePostBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.enumeration.AttachmentType
import ru.netology.nmedia.view.loadCircleCrop
import ru.netology.nmedia.viewmodel.PostViewModel

class SinglePostFragment: Fragment() {
    companion object{
        fun getNewInstance(post: Post): SinglePostFragment {
            val singlePostFragment = SinglePostFragment()
            singlePostFragment.arguments?.putParcelable("ARG_POST", post)
            return singlePostFragment
        }
    }

    private val viewModel: PostViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentSinglePostBinding.inflate(
            inflater,
            container,
            false
        )
        val callback = object : OnBackPressedCallback(true){

            override fun handleOnBackPressed() {
                try{
                    findNavController().navigateUp()
                }catch (e: Throwable){
                    println("aaaa $e")
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(callback)

        viewModel.data.observe(viewLifecycleOwner) {
            val postId =  arguments?.getParcelable<Post>("ARG_POST")?.id
            val post = it.posts.find{post -> post.id == postId}
            with(binding) {
                if (post != null) {
                    avatar.loadCircleCrop("${BuildConfig.BASE_URL}/avatars/${post.authorAvatar}")
//                    Glide.with(binding.avatar)
//                        .load(R.drawable.ic_avatar_foreground)
//                        .circleCrop()
//                        .into(binding.avatar)
                    author.text = post.author
                    published.text = post.published.toString()
                    content.text = post.content
                    likes.text = "${post.likes}"
                    shares.text = post.count(post.shares)
                    viewed.text = "${post.viewed}"

                    likes.setIconResource(
                        if (post.likedByMe) R.drawable.ic_liked_24 else R.drawable.ic_likes_24
                    )
                    likes.isChecked = post.likedByMe
                    likes.setIconTintResource(R.color.like_button_tint)
                    likes.setOnClickListener {
                        try {
                            viewModel.onLiked(post)
                        }catch (e: Throwable){
                            println("AAA $e")
                        }
                    }

                    shares.setOnClickListener {
                        viewModel.onShared(post)
                        shares.isChecked = false
                        val intent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, post.content)
                            type = "text/plain"
                        }
                        val shareIntent =
                            Intent.createChooser(intent, getString(R.string.chooser_share_post))
                        startActivity(shareIntent)
                        }

                    menu.setOnClickListener {
                        PopupMenu(it.context, it).apply {
                            inflate(R.menu.menu_post)
                            setOnMenuItemClickListener { item ->
                                when (item.itemId) {
                                    R.id.remove -> {
                                        viewModel.onRemove(post)
                                        findNavController().navigateUp()
                                        true
                                    }
                                    R.id.edit -> {
                                        viewModel.onEdit(post)
                                        val bundle = bundleOf("textArg" to post.content)
                                        findNavController().navigate(
                                            R.id.action_singlePostFragment_to_newPostFragment,
                                            bundle
                                        )
                                        true
                                    }
                                    else -> false
                                }
                            }
                        }.show()
                    }
                    if (post.attachment != null) {
                        when (post.attachment.type){
                            AttachmentType.VIDEO -> {
                                videoGroup.visibility = View.VISIBLE
                                video.setOnClickListener {
                                    val intent = Intent(Intent.ACTION_VIEW,
                                        Uri.parse(post.attachment.url))
                                    startActivity(intent)
                                }
                                videoPlay.setOnClickListener {
                                    val intent = Intent(Intent.ACTION_VIEW,
                                        Uri.parse(post.attachment.url))
                                    startActivity(intent)
                                }
                            }
                            AttachmentType.IMAGE ->{
                                Glide.with(imageAttachment)
                                    .load(Uri.parse("${post.attachment.url}"))
                                    .into(imageAttachment)
                            }
                        }
                    }
                }
            }
        }
        return binding.root
    }
}