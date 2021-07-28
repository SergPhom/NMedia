package ru.netology.nmedia.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import ru.netology.nmedia.databinding.FragmentNewPostBinding
import ru.netology.nmedia.util.AndroidUtils
import ru.netology.nmedia.util.StringArg
import ru.netology.nmedia.viewmodel.PostViewModel

class NewPostFragment : Fragment() {
    companion object{
        var Bundle.textArg: String? by StringArg
    }

    private val viewModel: PostViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ):View {

        val binding = FragmentNewPostBinding.inflate(
            inflater,
            container,
            false)

        binding.group.visibility = View.GONE
        binding.content.requestFocus()


        binding.content.setText(arguments?.getString("textArg"))
        if (!binding.content.text.isEmpty()) binding.group.visibility = View.VISIBLE
        //binding.content.setText(viewModel.edited.value?.content)

        binding.ok.setOnClickListener {
            viewModel.changeContent(binding.content.text.toString())
            viewModel.save()
            AndroidUtils.hideKeyboard(requireView())
            findNavController().navigateUp()
        }

        binding.cancelButton.setOnClickListener {
            with(binding.content){
                viewModel.cancel()
                setText("")
                clearFocus()
                AndroidUtils.hideKeyboard(this)
                binding.group.visibility = View.GONE
                findNavController().navigateUp()
            }

        }
        return binding.root
    }
}