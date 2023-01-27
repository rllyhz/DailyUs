package id.rllyhz.dailyus.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.facebook.shimmer.Shimmer
import com.facebook.shimmer.ShimmerDrawable
import com.google.android.material.imageview.ShapeableImageView
import id.rllyhz.dailyus.R
import id.rllyhz.dailyus.data.source.local.model.StoryEntity
import id.rllyhz.dailyus.databinding.ItemStoryBinding
import id.rllyhz.dailyus.utils.formatDate
import id.rllyhz.dailyus.utils.getTransitionName
import java.util.*

class StoriesPagingAdapter :
    PagingDataAdapter<StoryEntity, StoriesPagingAdapter.StoriesViewHolder>(DIFF_UTIL) {

    var onClick: ((imageView: ShapeableImageView, story: StoryEntity) -> Unit)? = null

    override fun onBindViewHolder(holder: StoriesViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(it)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoriesViewHolder =
        StoriesViewHolder(
            ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )

    inner class StoriesViewHolder(
        private val binding: ItemStoryBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                getItem(bindingAdapterPosition)?.let { story ->
                    onClick?.invoke(binding.itemStoryIvPost, story)
                }
            }
        }

        fun bind(story: StoryEntity) {
            val shimmerEffect = Shimmer
                .AlphaHighlightBuilder()
                .setDuration(1600)
                .setBaseAlpha(0.75f)
                .setHighlightAlpha(0.65f)
                .setDirection(Shimmer.Direction.LEFT_TO_RIGHT)
                .setAutoStart(true)
                .build()

            val shimmerDrawable = ShimmerDrawable().apply {
                setShimmer(shimmerEffect)
            }

            with(binding) {
                Glide.with(itemView.context)
                    .load(story.photoUrl)
                    .placeholder(shimmerDrawable)
                    .error(R.drawable.bg_rounded)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(itemStoryIvPost)

                itemStoryTvFullName.text = story.name.replaceFirstChar { firstChar ->
                    if (firstChar.isLowerCase()) firstChar.titlecase(Locale.getDefault())
                    else firstChar.toString()
                }

                itemStoryTvDate.text = formatDate(story.createdAt)
                itemStoryTvDescription.text = story.description

                val transitionNameOfImage =
                    itemView.context.getString(R.string.transition_name_of_image)
                itemStoryIvPost.transitionName = getTransitionName(transitionNameOfImage, story.id)
            }
        }
    }

    companion object {
        val DIFF_UTIL = object : DiffUtil.ItemCallback<StoryEntity>() {
            override fun areItemsTheSame(oldItem: StoryEntity, newItem: StoryEntity): Boolean =
                newItem.id == oldItem.id

            override fun areContentsTheSame(oldItem: StoryEntity, newItem: StoryEntity): Boolean =
                newItem == oldItem
        }
    }
}