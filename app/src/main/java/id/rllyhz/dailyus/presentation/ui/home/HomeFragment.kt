package id.rllyhz.dailyus.presentation.ui.home

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
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import id.rllyhz.dailyus.R
import id.rllyhz.dailyus.databinding.FragmentHomeBinding
import id.rllyhz.dailyus.presentation.adapter.StoriesAdapter
import id.rllyhz.dailyus.presentation.ui.main.MainViewModel
import id.rllyhz.dailyus.utils.hide
import id.rllyhz.dailyus.utils.show
import id.rllyhz.dailyus.vo.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private var binding: FragmentHomeBinding? = null
    private val viewModel: MainViewModel by activityViewModels()

    private var storiesAdapter: StoriesAdapter? = null

    private var job: Job? = null

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

        setUI()
        setAdapter()
        setViewModel()
    }

    private fun setUI() {
        binding?.run {
            homeBtnTryAgain.setOnClickListener {
                loadStories()
            }
        }

        loadStories()
    }

    private fun setViewModel() {
        binding?.run {
            viewModel.getFullName().observe(viewLifecycleOwner) {
                homeTvGreetingUser.text = getString(R.string.home_greeting_user, it)
            }
        }
    }

    private fun loadStories() {

        binding?.run {
            viewModel.getToken().observe(viewLifecycleOwner) { token ->

                job?.cancel()
                job = null

                job = lifecycleScope.launch(Dispatchers.IO) {
                    viewModel.fetchStories(token).collectLatest { resource ->
                        binding?.run {
                            when (resource) {
                                is Resource.Loading -> withContext(Dispatchers.Main) {
                                    homeProgressbarLoading.show()
                                    homeTvErrorStories.hide()
                                    homeRvStories.hide()
                                    homeBtnTryAgain.hide()
                                    Log.d("HomeFragment", "Loading: ${resource.message}")
                                }
                                is Resource.Error -> withContext(Dispatchers.Main) {
                                    homeProgressbarLoading.hide()
                                    homeTvErrorStories.show()
                                    homeBtnTryAgain.show()
                                    Log.e("HomeFragment", "Error: ${resource.message}")
                                }
                                is Resource.Success -> withContext(Dispatchers.Main) {
                                    val stories = resource.data

                                    homeProgressbarLoading.hide()
                                    homeTvErrorStories.hide()
                                    homeBtnTryAgain.hide()

                                    if (stories.isNullOrEmpty()) {
                                        homeTvErrorStories.text =
                                            getString(R.string.stories_empty_message)
                                        homeTvErrorStories.show()
                                        homeRvStories.hide()
                                    } else {
                                        storiesAdapter?.submitList(stories)
                                        waitForTransition(homeRvStories)
                                        homeRvStories.show()
                                    }
                                    Log.i("HomeFragment", "Success: ${resource.message}")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun setAdapter() {
        storiesAdapter = StoriesAdapter()
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
                    rv.adapter = sAdapter
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

        job?.cancel()
        job = null

        binding = null
        storiesAdapter = null
    }
}