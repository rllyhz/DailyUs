package id.rllyhz.dailyus.presentation.ui.home

import android.content.Intent
import android.os.Bundle
import android.transition.TransitionInflater
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import id.rllyhz.dailyus.R
import id.rllyhz.dailyus.databinding.FragmentHomeBinding
import id.rllyhz.dailyus.presentation.adapter.LoadPagingAdapter
import id.rllyhz.dailyus.presentation.adapter.StoriesPagingAdapter
import id.rllyhz.dailyus.presentation.ui.main.MainActivity
import id.rllyhz.dailyus.presentation.ui.main.MainViewModel
import id.rllyhz.dailyus.utils.hide
import id.rllyhz.dailyus.utils.show
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private var binding: FragmentHomeBinding? = null
    private val viewModel: MainViewModel by activityViewModels()

    private var storiesAdapter: StoriesPagingAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        sharedElementEnterTransition =
            TransitionInflater.from(context).inflateTransition(android.R.transition.move)
        sharedElementReturnTransition =
            TransitionInflater.from(context).inflateTransition(android.R.transition.move)

        binding = FragmentHomeBinding.inflate(inflater)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        postponeEnterTransition()

        setAdapter()
        setViewModel()
        setUI()
    }

    private fun setUI() {
        binding?.run {
            homeIvMapPost.setOnClickListener {
                Intent(requireActivity() as MainActivity, StoryMapActivity::class.java).also {
                    startActivity(it)
                }
            }
        }

        viewModel.getToken().observe(viewLifecycleOwner) { token ->
            if (token.isNotBlank() && token.isNotEmpty())
                lifecycleScope.launch {
                    viewModel.loadPagingStories(token).cancellable().collectLatest {
                        storiesAdapter?.submitData(it)
                    }
                }
        }
    }

    private fun setViewModel() = binding?.run {
        viewModel.scrollToTopEventCallback = {
            binding?.homeRvStories?.run {
                val ableToScrollVertically = layoutManager?.canScrollVertically() ?: false

                if (ableToScrollVertically)
                    smoothScrollToPosition(0)
            }
        }

        viewModel.getFullName().observe(viewLifecycleOwner) {
            homeTvGreetingUser.text = getString(R.string.home_greeting_user, it)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            storiesAdapter?.let { adapter ->
                adapter.loadStateFlow.collectLatest { loadStates ->
                    binding?.run {
                        when (loadStates.refresh) {
                            is LoadState.Loading -> {
                                homeProgressbarLoading.show()
                                homeTvErrorStories.hide()
                                homeRvStories.hide()
                                homeBtnTryAgain.hide()
                                Log.d("HomeFragment", "Loading...")
                            }
                            is LoadState.NotLoading -> {
                                homeProgressbarLoading.hide()
                                homeTvErrorStories.hide()
                                homeRvStories.show()
                                homeBtnTryAgain.hide()
                                waitForTransition(homeRvStories)
                            }
                            is LoadState.Error -> {
                                if (adapter.itemCount == 0) {
                                    homeProgressbarLoading.hide()
                                    homeTvErrorStories.text =
                                        getString(R.string.stories_empty_message)
                                    homeTvErrorStories.show()
                                    homeRvStories.hide()
                                    homeBtnTryAgain.show()
                                    homeBtnTryAgain.setOnClickListener { adapter.retry() }
                                } else {
                                    homeProgressbarLoading.hide()
                                    homeTvErrorStories.hide()
                                    homeRvStories.show()
                                    homeBtnTryAgain.hide()
                                    waitForTransition(homeRvStories)
                                    Log.e("HomeFragment", "Error: ${loadStates.refresh}")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun setAdapter() {
        storiesAdapter = StoriesPagingAdapter()
        storiesAdapter?.onClick = { imageView, story ->
            findNavController().navigate(
                R.id.action_homeFragment_to_detailFragment,
                Bundle().apply {
                    putParcelable(DetailFragment.STORY_KEY, story)
                },
                null,
                FragmentNavigatorExtras(
                    imageView to imageView.transitionName,
                )
            )
        }

        binding?.run {
            storiesAdapter?.let { sAdapter ->
                homeRvStories.also { rv ->
                    rv.adapter = sAdapter.withLoadStateFooter(
                        footer = LoadPagingAdapter { sAdapter.retry() }
                    )
                    rv.layoutManager = LinearLayoutManager(requireActivity())
                }
            }
        }
    }

    private fun waitForTransition(targetView: View) {
        targetView.doOnPreDraw { startPostponedEnterTransition() }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        binding = null
        storiesAdapter = null
    }
}