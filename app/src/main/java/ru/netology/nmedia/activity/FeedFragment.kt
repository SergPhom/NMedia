package ru.netology.nmedia

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import ru.netology.nmedia.activity.SinglePostFragment
import ru.netology.nmedia.adapter.Callback
import ru.netology.nmedia.adapter.PostsAdapter
import ru.netology.nmedia.databinding.FragmentFeedBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.model.FeedModel
import ru.netology.nmedia.viewmodel.PostViewModel


class FeedFragment : Fragment() {

    private val viewModel: PostViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ):View {
        val binding = FragmentFeedBinding.inflate(
            inflater,
            container,
            false
        )

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
                findNavController().navigate(R.id.action_feedFragment_to_newPostFragment,
                    bundleOf("textArg" to post.content))
            }

            override fun onPlay(post: Post) {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(post.attachment?.url))
                startActivity(intent)
            }

            override fun onSingleView(post: Post) {
                findNavController().navigate(R.id.action_feedFragment_to_singlePostFragment,
                    bundleOf("ARG_POST" to post))
            }

            override fun onSavingRetry(post: Post){
                viewModel.edited.value = post
                viewModel.save()
            }
        })
        binding.list.adapter = adapter

        //****************************************************************Observers
        viewModel.newerCount.observe(viewLifecycleOwner){
            binding.newerPosts.isVisible = it > 0
            binding.newerPosts.text =  "${getString(R.string.newer_posts)} - "+
                    " ${viewModel.newerCount.value}"
        }

        viewModel.data.observe(viewLifecycleOwner) { state ->
            val viewedPosts = state.posts.filter{it.viewed}
            val notTopPosition = adapter.itemCount > 0 && adapter.itemCount < state.posts.size
            adapter.submitList(viewedPosts){
                if(notTopPosition && !binding.newerPosts.isVisible) binding.list.smoothScrollToPosition(0)
            }
            binding.emptyText.isVisible = state.empty

        }
        viewModel.dataState.observe(viewLifecycleOwner) { state ->
            binding.progress.isVisible = state.loading
            binding.errorGroup.isVisible = state.error
            binding.refresh.isRefreshing = state.refreshing
            if(!state.msg.isNullOrBlank()){
                Snackbar.make(binding.feedMain,
                    "Error ${state.msg}. Please retry later.",
                    Snackbar.LENGTH_LONG)
                    .show()
            }
        }
        //**************************************************************Listeners
        binding.refresh.setOnRefreshListener {
            viewModel.refreshPosts()
            binding.refresh.isRefreshing = false
        }

        binding.newerPosts.setOnClickListener {
            binding.newerPosts.isVisible = false
            viewModel.markNewerPostsViewed()
        }

        binding.retryButton.setOnClickListener {
            viewModel.loadPosts()
        }

        binding.fab.setOnClickListener {
            findNavController().navigate(R.id.action_feedFragment_to_newPostFragment)
        }
        return binding.root
    }

}