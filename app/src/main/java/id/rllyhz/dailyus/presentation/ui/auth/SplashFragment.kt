package id.rllyhz.dailyus.presentation.ui.auth

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import id.rllyhz.dailyus.R
import id.rllyhz.dailyus.databinding.FragmentSplashBinding

@AndroidEntryPoint
class SplashFragment : Fragment() {
    private var binding: FragmentSplashBinding? = null
    private var splashRunnable: Runnable? = null

    private val splashDuration = 2500L

    private val viewModel: AuthViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSplashBinding.inflate(inflater)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        splashRunnable = Runnable {
            viewModel.isLoggedIn()
                .observe(viewLifecycleOwner) { alreadyLoggedIn ->
                    if (alreadyLoggedIn) {
                        findNavController().navigate(R.id.action_splashFragment_to_mainActivity)
                    } else {
                        findNavController().navigate(R.id.action_splashFragment_to_onBoardingFragment)
                    }
                }
        }

        splashRunnable?.let { Handler(Looper.getMainLooper()).postDelayed(it, splashDuration) }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        binding = null
        splashRunnable?.let { Handler(Looper.getMainLooper()).removeCallbacks(it) }
        splashRunnable = null
    }
}