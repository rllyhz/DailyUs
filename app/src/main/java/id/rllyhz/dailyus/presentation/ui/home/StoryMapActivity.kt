package id.rllyhz.dailyus.presentation.ui.home

import android.content.res.Resources
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import dagger.hilt.android.AndroidEntryPoint
import id.rllyhz.dailyus.R
import id.rllyhz.dailyus.data.source.local.model.StoryEntity
import id.rllyhz.dailyus.databinding.ActivityStoryMapBinding
import id.rllyhz.dailyus.presentation.ui.main.MainViewModel
import id.rllyhz.dailyus.utils.hide
import id.rllyhz.dailyus.utils.show
import id.rllyhz.dailyus.vo.Resource

@AndroidEntryPoint
class StoryMapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityStoryMapBinding
    private lateinit var mapFragment: SupportMapFragment

    private val viewModel: MainViewModel by viewModels()

    private var stories: List<StoryEntity>? = null
    private var map: GoogleMap? = null
    private var isMapReady = false
    private var reloadCounter = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoryMapBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        supportActionBar?.let {
            it.setBackgroundDrawable(
                ColorDrawable(
                    ContextCompat.getColor(
                        this,
                        R.color.my_purple_700
                    )
                )
            )
            it.setDisplayHomeAsUpEnabled(true)
        }

        binding.toolbar.title = resources.getString(R.string.title_home)
        binding.toolbar.setTitleTextAppearance(this, R.style.TitleTextStyle_Inverse)

        mapFragment = supportFragmentManager
            .findFragmentById(R.id.story_map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        viewModel.getToken().observe(this) { token ->
            if (token.isNotBlank() && token.isNotEmpty())
                viewModel.loadStoriesWithLocation(token)
        }

        viewModel.storiesWithLocation.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.storyMapProgressbar.show()
                    binding.storyMap.hide()
                    binding.storyMapTvError.hide()
                    Log.d("StoryMapActivity", "Loading: ${resource.message}")
                }
                is Resource.Error -> {
                    binding.storyMapProgressbar.hide()
                    binding.storyMap.hide()
                    binding.storyMapTvError.show()
                    Log.e("StoryMapActivity", "Error: ${resource.message}")
                }
                is Resource.Success -> {
                    stories = resource.data

                    binding.storyMap.hide()
                    binding.storyMapTvError.hide()

                    if (stories.isNullOrEmpty()) {
                        binding.storyMapProgressbar.hide()
                        binding.storyMapTvError.text =
                            getString(R.string.stories_empty_message)
                        binding.storyMapTvError.show()
                    } else {
                        shouldDrawMap()
                    }
                    Log.i("StoryMapActivity", "Success: ${resource.message}")
                }
                else -> Unit
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        isMapReady = true

        try {
            val success =
                map?.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))

            if (success != null && success) {
                Log.e("StoryMapActivity", "Style parsing failed.")
            }
        } catch (exception: Resources.NotFoundException) {
            Log.e("StoryMapActivity", "Can't find style. Error: ", exception)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    private fun shouldDrawMap() {
        if (!isMapReady) {

            if (reloadCounter > 3) {
                binding.storyMapProgressbar.hide()
                binding.storyMapTvError.text = getString(R.string.internal_error_message)
                return
            }

            Thread.sleep(2000L)
            shouldDrawMap()
        }

        Log.d("StoryMapActivity", "Map ready to draw!")
        binding.storyMapProgressbar.hide()
        binding.storyMapTvError.hide()
        binding.storyMap.show()

        map?.let { _map ->
            val boundsBuilder = LatLngBounds.Builder()

            stories?.forEach {
                if (it.latitude != null && it.longitude != null) {
                    val coordinate = LatLng(it.latitude, it.longitude)
                    _map.addMarker(
                        MarkerOptions().position(coordinate)
                            .title(it.name)
                            .snippet(it.description)
                    )
                    boundsBuilder.include(coordinate)
                }
            }

            Log.d("StoryMapActivity", "Map successfully drawn")

            val bounds = boundsBuilder.build()
            _map.animateCamera(
                CameraUpdateFactory.newLatLngBounds(
                    bounds,
                    resources.displayMetrics.widthPixels,
                    resources.displayMetrics.heightPixels,
                    120
                )
            )
        }
    }
}