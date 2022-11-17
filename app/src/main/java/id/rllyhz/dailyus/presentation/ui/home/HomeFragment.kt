package id.rllyhz.dailyus.presentation.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import id.rllyhz.dailyus.R
import id.rllyhz.dailyus.databinding.FragmentHomeBinding
import id.rllyhz.dailyus.presentation.adapter.BaseLoadingPagingAdapter
import id.rllyhz.dailyus.presentation.adapter.StoriesAdapter
import id.rllyhz.dailyus.presentation.ui.main.MainViewModel
import id.rllyhz.dailyus.utils.hide
import id.rllyhz.dailyus.utils.show
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private var binding: FragmentHomeBinding? = null
    private val viewModel: MainViewModel by activityViewModels()

    private var baseAdapter: BaseLoadingPagingAdapter? = null
    private var storiesAdapter: StoriesAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setAdapter()

        binding?.run {
            viewModel.getFullName().observe(viewLifecycleOwner) {
                homeTvGreetingUser.text = getString(R.string.home_greeting_user, it)
            }

            loadData()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        binding = null
        baseAdapter = null
        storiesAdapter = null
    }

    private fun loadData() {
        viewModel.getToken().observe(viewLifecycleOwner) { token ->
            lifecycleScope.launch {
                viewModel.getStories(token).collectLatest {
                    storiesAdapter?.submitData(it)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            storiesAdapter?.loadStateFlow?.collectLatest { loadState ->
                binding?.run {
                    when (loadState.refresh) {
                        is LoadState.Loading -> {
                            homeProgressbarLoading.show()
                            homeTvEmptyStories.hide()
                            homeTvErrorStories.hide()
                            homeRvStories.hide()
                            homeBtnTryAgain.hide()
                        }
                        is LoadState.Error -> {
                            if (storiesAdapter != null && storiesAdapter?.itemCount == 0) {
                                homeProgressbarLoading.hide()
                                homeRvStories.hide()
                                homeTvErrorStories.hide()
                                homeTvEmptyStories.show()
                                homeBtnTryAgain.setOnClickListener {
                                    storiesAdapter?.retry()
                                }
                                homeBtnTryAgain.show()
                            } else {
                                homeProgressbarLoading.hide()
                                homeTvEmptyStories.hide()
                                homeTvErrorStories.show()
                                homeBtnTryAgain.setOnClickListener {
                                    storiesAdapter?.retry()
                                }
                                homeBtnTryAgain.show()
                                Log.e("HomeFragment", "Error: ${loadState.refresh}")
                            }
                        }
                        is LoadState.NotLoading -> {
                            homeProgressbarLoading.hide()
                            homeTvEmptyStories.hide()
                            homeTvErrorStories.hide()
                            homeBtnTryAgain.hide()
                            homeRvStories.show()
                        }
                    }
                }
            }
        }
    }

    private fun setAdapter() {
        storiesAdapter = StoriesAdapter()
        storiesAdapter?.onClick = {
            findNavController().navigate(
                R.id.action_homeFragment_to_detailFragment,
                Bundle().apply {
                    putParcelable(DetailFragment.STORY_KEY, it)
                }
            )
        }

        binding?.run {
            storiesAdapter?.let { sAdapter ->
                homeRvStories.also { rv ->
                    rv.adapter = sAdapter.withLoadStateFooter(
                        footer = BaseLoadingPagingAdapter { sAdapter.retry() }
                    )
                    rv.layoutManager = LinearLayoutManager(requireActivity())
                }
            }
        }
    }
}