package id.rllyhz.dailyus.presentation.ui.auth

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import id.rllyhz.dailyus.R
import id.rllyhz.dailyus.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {
    private var binding: FragmentLoginBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.run {
            layoutTopBar.topBarTvTitle.text = getString(R.string.title_login)
            layoutTopBar.topBarBtnBack.setOnClickListener { findNavController().navigateUp() }

            loginEtEmail.setErrorMessage(getString(R.string.email_invalid_message))
            loginEtPassword.setErrorMessage(getString(R.string.password_invalid_message))

            val submitBtn =
                ObjectAnimator.ofFloat(loginBtnSubmit, View.ALPHA, 1f)
                    .setDuration(1000).apply {
                        startDelay = 150
                    }

            AnimatorSet().apply {
                play(submitBtn)
            }.start()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}