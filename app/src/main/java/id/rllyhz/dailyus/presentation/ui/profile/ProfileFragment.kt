package id.rllyhz.dailyus.presentation.ui.profile

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import id.rllyhz.dailyus.R
import id.rllyhz.dailyus.databinding.FragmentProfileBinding
import id.rllyhz.dailyus.presentation.ui.main.MainViewModel

@AndroidEntryPoint
class ProfileFragment : Fragment() {
    private var binding: FragmentProfileBinding? = null
    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fullName = viewModel.getFullName()
        val email = viewModel.getEmail()

        binding?.run {
            fullName.observe(viewLifecycleOwner) {
                profileTvFullName.text = it
            }
            email.observe(viewLifecycleOwner) {
                profileTvEmail.text = it
            }

            profileBtnLogout.setOnClickListener {
                viewModel.logout()

                requireActivity().finish()
                findNavController().navigate(R.id.action_profileFragment_to_authActivity)
            }

            val progressBarAnim = ObjectAnimator.ofFloat(profileProgressbar, View.ALPHA, 0f)
                .setDuration(500).apply { startDelay = 400 }
            val tvFullNameAnim =
                ObjectAnimator.ofFloat(profileTvFullName, View.ALPHA, 1f)
                    .setDuration(1000).apply { startDelay = 400 }
            val tvEmailAnim =
                ObjectAnimator.ofFloat(profileTvEmail, View.ALPHA, 1f).setDuration(1000)
                    .apply { startDelay = 800 }
            val btnLogoutAnim =
                ObjectAnimator.ofFloat(profileBtnLogout, View.ALPHA, 1f).setDuration(1000)
                    .apply { startDelay = 1200 }

            AnimatorSet().apply {
                playTogether(progressBarAnim, tvFullNameAnim, tvEmailAnim, btnLogoutAnim)
            }.start()
        }
    }
}