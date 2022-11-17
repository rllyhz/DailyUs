package id.rllyhz.dailyus.presentation.ui.profile

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
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

            profileBtnChangeLanguage.setOnClickListener {
                startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
            }

            profileBtnLogout.setOnClickListener {
                viewModel.logout()

                requireActivity().finish()
                findNavController().navigate(R.id.action_profileFragment_to_authActivity)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}