package id.rllyhz.dailyus.presentation.ui.auth

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import id.rllyhz.dailyus.R
import id.rllyhz.dailyus.data.source.remote.model.AuthLoginResult
import id.rllyhz.dailyus.databinding.FragmentLoginBinding
import id.rllyhz.dailyus.presentation.ui.main.MainActivity
import id.rllyhz.dailyus.utils.*
import id.rllyhz.dailyus.vo.Resource
import id.rllyhz.dailyus.vo.UIState

@AndroidEntryPoint
class LoginFragment : Fragment() {
    private var binding: FragmentLoginBinding? = null
    private val viewModel: AuthViewModel by activityViewModels()

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

            loginBtnSubmit.setOnClickListener { validateLoginForm() }

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

    private fun validateLoginForm() {
        binding?.run {
            (requireActivity().getSystemService(
                Context.INPUT_METHOD_SERVICE,
            ) as InputMethodManager).apply {
                hideSoftInputFromWindow(view?.windowToken, 0)
            }

            val email = loginEtEmail.text.toString()
            val password = loginEtPassword.text.toString()

            when {
                email.isEmpty() -> {
                    loginEtEmail.error = getString(R.string.email_empty_message)
                    loginEtEmail.requestFocus()
                }
                password.isEmpty() -> {
                    loginEtPassword.error = getString(R.string.password_empty_message)
                    loginEtPassword.requestFocus()
                }
                else -> {
                    loginEtEmail.error = null
                    loginEtPassword.error = null
                    submitLoginData(email, password)
                }
            }
        }
    }

    private fun submitLoginData(userEmail: String, userPassword: String) {
        viewModel.login(userEmail, userPassword).observe(viewLifecycleOwner) { resultResources ->
            when (resultResources) {
                is Resource.Error -> {
                    updateUI(UIState.Error, resultResources.message)
                    println("LoginFragment" + resultResources.message)
                }
                is Resource.Loading -> {
                    updateUI(UIState.Loading, null)
                }
                is Resource.Success -> {
                    updateUI(UIState.HasData, null)

                    saveLoggedInUserData(resultResources.data!!.authLoginResult, userEmail)

                    Intent(requireContext(), MainActivity::class.java).also {
                        startActivity(it)
                        requireActivity().finish()
                    }
                }
            }
        }
    }

    private fun saveLoggedInUserData(authLoginResult: AuthLoginResult, userEmail: String) {
        val userToken = authLoginResult.token
        val userFullName = authLoginResult.name

        viewModel.saveLoggedInUserData(userToken, userFullName, userEmail)
    }

    private fun updateUI(uiState: UIState, resourceMessage: String?) {
        when (uiState) {
            UIState.Error -> {
                binding?.loginProgressbar?.hide()
                binding?.loginBtnSubmit?.clickable()
                binding?.loginBtnSubmit?.text = getString(R.string.button_login)
            }
            UIState.Loading -> {
                binding?.loginBtnSubmit?.text = ""
                binding?.loginProgressbar?.show()
                binding?.loginBtnSubmit?.notClickable()
            }
            UIState.HasData -> {
                binding?.loginProgressbar?.hide()
                binding?.loginBtnSubmit?.clickable()
                binding?.loginBtnSubmit?.text = getString(R.string.button_login)
            }
        }

        resourceMessage?.let {
            binding?.let { bnView ->
                if (it == "HTTP 401 Unauthorized") {
                    showAuthSnackBar(
                        requireActivity(),
                        bnView.root,
                        bnView.loginBtnSubmit,
                        getString(R.string.wrong_credential_message)
                    )
                } else {
                    showAuthSnackBar(
                        requireActivity(),
                        bnView.root,
                        bnView.loginBtnSubmit,
                        getString(R.string.internal_error_message)
                    )
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}