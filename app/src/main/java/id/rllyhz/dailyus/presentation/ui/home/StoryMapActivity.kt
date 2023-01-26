package id.rllyhz.dailyus.presentation.ui.home

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import id.rllyhz.dailyus.R
import id.rllyhz.dailyus.databinding.ActivityStoryMapBinding
import id.rllyhz.dailyus.utils.hide
import id.rllyhz.dailyus.utils.show

class StoryMapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityStoryMapBinding
    private lateinit var mapFragment: SupportMapFragment

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
    }

    override fun onMapReady(map: GoogleMap) {
        with(binding) {
            progressbar.hide()
            Thread.sleep(3000L)
            storyMap.show()
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
}