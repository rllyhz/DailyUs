package id.rllyhz.dailyus.presentation.ui.post

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import id.rllyhz.dailyus.databinding.ActivityStoryMapBinding

class StoryMapActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStoryMapBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoryMapBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}