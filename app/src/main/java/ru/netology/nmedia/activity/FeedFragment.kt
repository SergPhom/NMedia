package ru.netology.nmedia

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.paging.flatMap
import androidx.paging.map
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import ru.netology.nmedia.adapter.Callback
import ru.netology.nmedia.adapter.PostsAdapter
import ru.netology.nmedia.databinding.FragmentFeedBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.viewmodel.PostViewModel
import javax.inject.Inject

@AndroidEntryPoint
class FeedFragment: Fragment() {

    private val viewModel: PostViewModel by viewModels(
        ownerProducer = ::requireParentFragment)

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
//               if(viewModel.authenticated.value == true) viewModel.onLiked(post)
//               else binding.signInDialog.visibility = View.VISIBLE
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

            override fun onSingleViewImageOnly(post: Post){
                findNavController().navigate(R.id.action_feedFragment_to_singlePostFragment,
                    bundleOf("ARG_POST" to post.copy(id=0))  )
            }

            override fun onSavingRetry(post: Post){
                viewModel.edited.value = post
                viewModel.save()
            }
        })
        binding.list.adapter = adapter

        //****************************************************************Observers
//        viewModel.newerCount.observe(viewLifecycleOwner){
//            binding.newerPosts.isVisible = it > 0
//            binding.newerPosts.text =  "${getString(R.string.newer_posts)} - "+
//                    " ${viewModel.newerCount.value}"
//        }

        lifecycleScope.launchWhenCreated {
//            viewModel.data.collectLatest(adapter::submitData)
//            viewModel.data.collectLatest{  println("FF $it")}
            viewModel.data.collectLatest {
                println("FF start collecting, paging data is $it")
                try {
                    adapter.submitData(it)
                } catch (e: Throwable) {
                    println("FF err ${e.stackTrace}")
                }
                println("FF paging done")
            }


//            { state ->
//                val viewedPosts = state.posts.filter{it.viewed}
//                val notTopPosition = adapter.itemCount > 0 && adapter.itemCount < state.posts.size
//                adapter.submitData(viewedPosts){
//                    if(notTopPosition && !binding.newerPosts.isVisible) binding.list.smoothScrollToPosition(0)
//                }
//                binding.emptyText.isVisible = state
//
//            }
        }

        lifecycleScope.launchWhenCreated {
            adapter.loadStateFlow.collectLatest { state ->
                binding.refresh.isRefreshing =
                    state.refresh is LoadState.Loading ||
                    state.prepend is LoadState.Loading ||
                    state.append is LoadState.Loading
            }
        }

//        viewModel.dataState.observe(viewLifecycleOwner) { state ->
//            binding.progress.isVisible = state.loading
//            binding.errorGroup.isVisible = state.error
//            binding.refresh.isRefreshing = state.refreshing
//            if(!state.msg.isNullOrBlank()){
//                Snackbar.make(binding.root,
//                    "Error ${state.msg}. Please retry later.",
//                    Snackbar.LENGTH_INDEFINITE)
//                    .show()
//            }
//        }
        viewModel.authenticated.observe(viewLifecycleOwner){
//            println("FF auth is $it ")
        }

        //**************************************************************Listeners
        binding.refresh.setOnRefreshListener(adapter::refresh)

        binding.newerPosts.setOnClickListener {
            binding.newerPosts.isVisible = false
            viewModel.markNewerPostsViewed()
        }

        binding.retryButton.setOnClickListener {
            viewModel.loadPosts()
        }

        binding.fab.setOnClickListener {
            if(viewModel.authenticated.value == true){
                viewModel.forAuthenticated()
                findNavController().navigate(R.id.action_feedFragment_to_newPostFragment)
            } else{
                binding.signInDialog.visibility = View.VISIBLE
            }
        }
        binding.signInDialogOk.setOnClickListener {
            findNavController().navigate(
                R.id.action_feedFragment_to_authFragment,
            )
        }
        binding.signInDialogCancel.setOnClickListener{
            binding.signInDialog.visibility = View.GONE
        }
        return binding.root
    }

}