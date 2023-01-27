package id.rllyhz.dailyus.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import id.rllyhz.dailyus.databinding.ItemLoadPagingBinding

class LoadPagingAdapter(
    private val retry: () -> Unit
) : LoadStateAdapter<LoadPagingAdapter.LoadingViewHolder>() {

    override fun onBindViewHolder(holder: LoadingViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): LoadingViewHolder =
        LoadingViewHolder(
            ItemLoadPagingBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            retry
        )

    inner class LoadingViewHolder(
        private val binding: ItemLoadPagingBinding,
        retry: () -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.itemLoadPagingBtnRetry.setOnClickListener { retry.invoke() }
        }

        fun bind(loadState: LoadState) {
            if (loadState is LoadState.Error) {
                binding.itemLoadPagingTvErrorMsg.text = loadState.error.localizedMessage
            }
            binding.apply {
                itemLoadPagingProgressbar.isVisible = loadState is LoadState.Loading
                itemLoadPagingTvErrorMsg.isVisible = loadState is LoadState.Error
                itemLoadPagingBtnRetry.isVisible = loadState is LoadState.Error
            }
        }
    }
}