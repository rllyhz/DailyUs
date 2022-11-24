package id.rllyhz.dailyus.presentation.ui.post

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Context.INPUT_METHOD_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import id.rllyhz.dailyus.R
import id.rllyhz.dailyus.databinding.FragmentPostBinding
import id.rllyhz.dailyus.presentation.ui.main.MainViewModel
import id.rllyhz.dailyus.presentation.ui.post.CameraActivity.Companion.CAMERA_X_RESULT_CODE
import id.rllyhz.dailyus.presentation.ui.post.CameraActivity.Companion.IMAGE_FILE_EXTRA
import id.rllyhz.dailyus.presentation.ui.post.CameraActivity.Companion.IS_BACK_CAMERA_MODE_EXTRA
import id.rllyhz.dailyus.utils.*
import id.rllyhz.dailyus.vo.Resource
import id.rllyhz.dailyus.vo.UIState
import java.io.File

@AndroidEntryPoint
class PostFragment : Fragment() {
    private var binding: FragmentPostBinding? = null
    private val viewModel: MainViewModel by viewModels()

    private var isPhotoFromCamera = false

    private var photoResult: Bitmap? = null
    private var file: File? = null

    private val cameraRequestPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (!granted) {
            updateUI(UIState.Error, "Permission tidak diizinkan :(")
        }
    }

    private val launcherIntentForCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CAMERA_X_RESULT_CODE) {
            val resultFile = it.data?.getSerializableExtra(IMAGE_FILE_EXTRA) as File
            val isBackCamera = it.data?.getBooleanExtra(IS_BACK_CAMERA_MODE_EXTRA, true) as Boolean

            file = resultFile
            file?.let { _file ->
                photoResult = _file.getBitmap(isBackCamera)
                isPhotoFromCamera = true
            }

            binding?.run {
                postProgressbar.hide()
                postIvPreviewImage.setImageBitmap(photoResult)
                postIvPreviewImage.show()
                postBtnUpload.clickable()
            }
        }
    }

    private val launcherIntentForPickingFromGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            val resourceUri = it.data?.data as Uri
            file = resourceUri.toFile(requireContext())
            isPhotoFromCamera = false

            binding?.run {
                postProgressbar.hide()
                postIvPreviewImage.setImageURI(resourceUri)
                postIvPreviewImage.show()
                postBtnUpload.clickable()
            }
        }
    }

    private fun isCameraPermissionGranted() = ALL_REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            requireContext(), it
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPostBinding.inflate(inflater)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (!isCameraPermissionGranted()) {
            cameraRequestPermission.launch(ALL_REQUIRED_PERMISSIONS[0])
        }

        viewModel.uploadStoryResponse.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> updateUI(UIState.Loading, null)
                is Resource.Error -> updateUI(
                    UIState.Error,
                    if (viewModel.shouldHandleUploadStoryEvent()) getString(R.string.upload_failed_message) else null
                )
                is Resource.Success -> updateUI(
                    UIState.HasData,
                    if (viewModel.shouldHandleUploadStoryEvent()) getString(R.string.upload_success_message) else null
                )
            }
        }

        binding?.run {
            postProgressbar.hide()

            postBtnTakePicture.setOnClickListener { takePicture() }
            postBtnPickFromGallery.setOnClickListener { pickPhotoFromGallery() }

            postBtnUpload.setOnClickListener {
                postEtDescription.clearFocus()

                (requireActivity().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager).also {
                    it.hideSoftInputFromWindow(view.windowToken, 0)
                }

                val description = postEtDescription.text.toString()

                if ((isPhotoFromCamera && photoResult == null) || file == null) {
                    showPostSnackBar(
                        requireContext(),
                        root,
                        postBtnUpload,
                        getString(R.string.photo_empty_message)
                    )
                } else if (description.isEmpty()) {
                    showPostSnackBar(
                        requireContext(),
                        root,
                        postBtnUpload,
                        getString(R.string.description_empty_message)
                    )
                } else {
                    uploadNewStory(description)
                }
            }

            updateUI(UIState.HasData, null)
        }
    }

    private fun uploadNewStory(description: String) = file?.let {
        viewModel.uploadStory(it, it.name, description) { size ->
            updateUI(
                UIState.Error,
                getString(R.string.upload_image_size_too_large_message) + " ($size mb)"
            )
        }
    }

    private fun pickPhotoFromGallery() {
        val intent = Intent().apply {
            type = "image/*"
            action = Intent.ACTION_GET_CONTENT
        }

        val chooser = Intent.createChooser(intent, getString(R.string.title_pick_from_gallery))
        launcherIntentForPickingFromGallery.launch(chooser)
    }

    private fun takePicture() {
        if (!isCameraPermissionGranted()) {
            cameraRequestPermission.launch(ALL_REQUIRED_PERMISSIONS[0])
            return
        }

        Intent(requireActivity(), CameraActivity::class.java).also {
            launcherIntentForCameraX.launch(it)
        }
    }

    private fun updateUI(uiState: UIState, messageToShow: String?) {
        binding?.run {
            when (uiState) {
                UIState.Loading -> {
                    postBtnUpload.notClickable()
                    postProgressbar.show()
                    postBtnTakePicture.notClickable()
                    postBtnPickFromGallery.notClickable()
                    postBtnUpload.text = ""
                }
                UIState.Error -> {
                    postBtnUpload.clickable()
                    postProgressbar.hide()
                    postBtnTakePicture.clickable()
                    postBtnPickFromGallery.clickable()
                    postBtnUpload.text = getString(R.string.button_upload)
                }
                UIState.HasData -> {
                    postBtnUpload.clickable()
                    postProgressbar.hide()
                    postBtnTakePicture.clickable()
                    postBtnPickFromGallery.clickable()
                    postEtDescription.text.clear()
                    postIvPreviewImage.setImageResource(0)
                    postIvPreviewImage.background =
                        ColorDrawable(
                            ContextCompat.getColor(requireContext(), R.color.my_grey_200)
                        )
                    photoResult = null
                    file = null
                    postBtnUpload.text = getString(R.string.button_upload)
                }
            }

            messageToShow?.let {
                showPostSnackBar(
                    requireContext(),
                    root,
                    postBtnUpload,
                    it
                )

                postBtnUpload.clickable()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
        photoResult = null
        file = null
    }

    companion object {
        val ALL_REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }
}