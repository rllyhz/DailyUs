package id.rllyhz.dailyus.presentation.ui.auth

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import id.rllyhz.dailyus.R
import id.rllyhz.dailyus.databinding.FragmentRegisterBinding
import id.rllyhz.dailyus.utils.*
import id.rllyhz.dailyus.vo.Resource
import id.rllyhz.dailyus.vo.UIState

@AndroidEntryPoint
class RegisterFragment : Fragment() {
    private var binding: FragmentRegisterBinding? = null
    private val viewModel: AuthViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegisterBinding.inflate(inflater)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.run {
            layoutTopBar.topBarTvTitle.text = getString(R.string.title_login)
            layoutTopBar.topBarBtnBack.setOnClickListener { findNavController().navigateUp() }

            registerEtEmail.setErrorMessage(getString(R.string.email_invalid_message))
            registerEtPassword.setErrorMessage(getString(R.string.password_invalid_message))

            registerBtnSubmit.setOnClickListener { validateRegisterForm() }

            val submitBtn =
                ObjectAnimator.ofFloat(registerBtnSubmit, View.ALPHA, 1f)
                    .setDuration(1000).apply {
                        startDelay = 150
                    }

            AnimatorSet().apply {
                play(submitBtn)
            }.start()
        }
    }

    private fun validateRegisterForm() {
        binding?.run {
            (requireActivity().getSystemService(
                Context.INPUT_METHOD_SERVICE,
            ) as InputMethodManager).apply {
                hideSoftInputFromWindow(view?.windowToken, 0)
            }

            val name = registerEtName.text.toString()
            val email = registerEtEmail.text.toString()
            val password = registerEtPassword.text.toString()

            when {
                name.isEmpty() -> {
                    registerEtName.error = getString(R.string.name_empty_message)
                    registerEtName.requestFocus()
                }
                email.isEmpty() -> {
                    registerEtEmail.error = getString(R.string.email_empty_message)
                    registerEtEmail.requestFocus()
                }
                password.isEmpty() -> {
                    registerEtPassword.error = getString(R.string.password_empty_message)
                    registerEtPassword.requestFocus()
                }
                else -> {
                    registerEtName.error = null
                    registerEtEmail.error = null
                    registerEtPassword.error = null
                    submitRegisterData(name, email, password)
                }
            }
        }
    }

    private fun submitRegisterData(newName: String, newEmail: String, newPassword: String) {
        viewModel.register(newName, newEmail, newPassword)
            .observe(viewLifecycleOwner) { resultResources ->
                when (resultResources) {
                    is Resource.Error -> {
                        updateUI(UIState.Error, resultResources.message)
                        println("RegisterFragment: " + resultResources.message)
                    }
                    is Resource.Loading -> {
                        updateUI(UIState.Loading, null)
                    }
                    is Resource.Success -> {
                        updateUI(UIState.HasData, null)

                        AlertDialog.Builder(requireContext())
                            .setTitle(getString(R.string.dialog_title_register_success))
                            .setCancelable(false)
                            .setMessage(getString(R.string.dialog_message_register_success))
                            .setPositiveButton(getString(R.string.dialog_positive_action_register_success)) { dialog, _ ->
                                dialog.dismiss()
                                findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
                            }
                            .show()
                    }
                }
            }
    }

    private fun updateUI(uiState: UIState, resourceMessage: String?) {
        when (uiState) {
            UIState.Error -> {
                binding?.registerProgressbar?.hide()
                binding?.registerBtnSubmit?.clickable()
                binding?.registerBtnSubmit?.text = getString(R.string.button_register)
            }
            UIState.Loading -> {
                binding?.registerBtnSubmit?.text = ""
                binding?.registerProgressbar?.show()
                binding?.registerBtnSubmit?.notClickable()
            }
            UIState.HasData -> {
                binding?.registerProgressbar?.hide()
                binding?.registerBtnSubmit?.clickable()
                binding?.registerBtnSubmit?.text = getString(R.string.button_register)
            }
        }

        resourceMessage?.let {
            binding?.let { bnView ->
                if (it == "HTTP 401 Unauthorized") {
                    showAuthSnackBar(
                        requireActivity(),
                        bnView.root,
                        bnView.registerBtnSubmit,
                        getString(R.string.wrong_credential_message)
                    )
                } else if (it == "Email is already taken") {
                    showAuthSnackBar(
                        requireActivity(),
                        bnView.root,
                        bnView.registerBtnSubmit,
                        getString(R.string.email_already_taken_message)
                    )
                } else {
                    showAuthSnackBar(
                        requireActivity(),
                        bnView.root,
                        bnView.registerBtnSubmit,
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