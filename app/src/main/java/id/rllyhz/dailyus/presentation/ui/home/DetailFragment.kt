package id.rllyhz.dailyus.presentation.ui.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.facebook.shimmer.Shimmer
import com.facebook.shimmer.ShimmerDrawable
import id.rllyhz.dailyus.R
import id.rllyhz.dailyus.data.source.local.model.StoryEntity
import id.rllyhz.dailyus.databinding.FragmentDetailBinding
import id.rllyhz.dailyus.presentation.ui.main.MainActivity
import id.rllyhz.dailyus.utils.formatDate
import java.util.*

class DetailFragment : Fragment() {
    private var binding: FragmentDetailBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDetailBinding.inflate(inflater)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.run {
            layoutTopBar.topBarTvTitle.text = getString(R.string.title_detail)
            layoutTopBar.topBarBtnBack.setOnClickListener { findNavController().navigateUp() }

            val story = arguments?.getParcelable<StoryEntity>(STORY_KEY)

            story?.name?.let {
                detailTvFullName.text = it.replaceFirstChar { firstChar ->
                    if (firstChar.isLowerCase()) firstChar.titlecase(Locale.getDefault())
                    else firstChar.toString()
                }
            }

            story?.photoUrl?.let { photo ->
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

                Glide.with(requireContext())
                    .load(photo)
                    .placeholder(shimmerDrawable)
                    .error(R.drawable.bg_rounded)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(detailIvImage)
            }

            detailTvDescription.text = story?.description
            detailTvDate.text = formatDate(story?.createdAt)

            detailTvLatLon.text = if (story?.latitude != null && story.longitude != null) {
                getString(
                    R.string.detail_lat_lon,
                    story.latitude.toString(),
                    story.longitude.toString()
                )
            } else "-"
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity() as MainActivity).hideBottomNav()
    }

    override fun onDetach() {
        super.onDetach()
        (requireActivity() as MainActivity).showBottomNav()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    companion object {
        const val STORY_KEY = "STORY_KEY"
    }
}