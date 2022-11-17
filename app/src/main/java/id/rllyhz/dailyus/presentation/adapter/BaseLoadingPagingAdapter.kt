package id.rllyhz.dailyus.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import id.rllyhz.dailyus.databinding.ItemBaseLoadingPagingBinding

class BaseLoadingPagingAdapter(
    private val retry: () -> Unit
) : LoadStateAdapter<BaseLoadingPagingAdapter.BasePagingViewHolder>() {

    override fun onBindViewHolder(holder: BasePagingViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): BasePagingViewHolder =
        BasePagingViewHolder(
            ItemBaseLoadingPagingBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            retry
        )

    // ViewModel
    inner class BasePagingViewHolder(
        private val binding: ItemBaseLoadingPagingBinding, retry: () -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.itemBasePagingBtnRetryButton.setOnClickListener {
                retry.invoke()
            }
        }

        fun bind(loadState: LoadState) {
            if (loadState is LoadState.Error) {
                binding.itemBasePagingTvErrorMsg.text = loadState.error.localizedMessage
            }

            with(binding) {
                itemBasePagingProgressBar.isVisible = loadState is LoadState.Loading
                itemBasePagingTvErrorMsg.isVisible = loadState is LoadState.Error
                itemBasePagingBtnRetryButton.isVisible = loadState is LoadState.Error
            }
        }
    }
}