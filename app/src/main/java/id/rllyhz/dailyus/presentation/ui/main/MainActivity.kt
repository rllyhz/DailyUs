package id.rllyhz.dailyus.presentation.ui.main

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import dagger.hilt.android.AndroidEntryPoint
import id.rllyhz.dailyus.R
import id.rllyhz.dailyus.databinding.ActivityMainBinding
import id.rllyhz.dailyus.utils.hide
import id.rllyhz.dailyus.utils.show

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        binding.bottomNavMain.setupWithNavController(navController)

        binding.bottomNavMain.setOnItemSelectedListener { menu ->
            when (menu.itemId) {
                R.id.homeFragment -> {
                    navController.navigate(R.id.homeFragment)
                }
                R.id.postFragment -> {
                    navController.navigate(R.id.postFragment)
                }
                R.id.profileFragment -> {
                    navController.navigate(R.id.profileFragment)
                }
            }
            true
        }

        binding.bottomNavMain.setOnItemReselectedListener {
            when (it.itemId) {
                R.id.homeFragment -> {
                    viewModel.addScrollToTopEvent()
                }
            }
        }
    }

    fun getBottomNav() = binding.bottomNavMain

    fun showBottomNav() {
        binding.bottomNavMain.show()
    }

    fun hideBottomNav() {
        binding.bottomNavMain.hide()
    }

    override fun onSupportNavigateUp(): Boolean {
        return false
    }
}