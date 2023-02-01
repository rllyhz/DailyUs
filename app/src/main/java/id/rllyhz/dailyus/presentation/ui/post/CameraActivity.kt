package id.rllyhz.dailyus.presentation.ui.post

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import id.rllyhz.dailyus.R
import id.rllyhz.dailyus.databinding.ActivityCameryBinding
import id.rllyhz.dailyus.utils.createImageFile
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCameryBinding
    private lateinit var cameraExecutor: ExecutorService

    private var imageCapture: ImageCapture? = null
    private var cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cameraExecutor = Executors.newSingleThreadExecutor()

        with(binding) {
            cameraIvTakePicture.setOnClickListener { takePicture() }
            cameraIvSwitchCamera.setOnClickListener {
                cameraSelector = if (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) {
                    CameraSelector.DEFAULT_FRONT_CAMERA
                } else CameraSelector.DEFAULT_BACK_CAMERA

                startCamera()
            }
        }
    }

    private fun startCamera() {
        ProcessCameraProvider.getInstance(this).apply {
            addListener({
                val cameraProvider = get()
                val preview = Preview.Builder()
                    .build()
                    .apply {
                        setSurfaceProvider(binding.cameraPreviewView.surfaceProvider)
                    }

                imageCapture = ImageCapture.Builder().build()

                try {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        this@CameraActivity,
                        cameraSelector,
                        preview,
                        imageCapture
                    )
                } catch (e: Exception) {
                    showToast(getString(R.string.internal_error_message))
                }
            }, ContextCompat.getMainExecutor(this@CameraActivity))
        }
    }

    private fun takePicture() {
        imageCapture?.let {
            val photoFile = createImageFile(application)

            val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
            it.takePicture(
                outputOptions,
                ContextCompat.getMainExecutor(this),
                object : ImageCapture.OnImageSavedCallback {
                    override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                        val intent = Intent().apply {
                            putExtra(IMAGE_FILE_EXTRA, photoFile)
                            putExtra(
                                IS_BACK_CAMERA_MODE_EXTRA,
                                cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA
                            )
                        }

                        setResult(CAMERA_X_RESULT_CODE, intent)
                        finish()
                    }

                    override fun onError(exception: ImageCaptureException) {
                        showToast(getString(R.string.internal_error_message))
                    }
                }
            )
        }
    }

    private fun hideSystemUI() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onResume() {
        super.onResume()
        hideSystemUI()
        startCamera()
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    companion object {
        const val CAMERA_X_RESULT_CODE = 180

        const val IMAGE_FILE_EXTRA = "IMAGE_FILE_EXTRA"
        const val IS_BACK_CAMERA_MODE_EXTRA = "IS_BACK_CAMERA_MODE_EXTRA"
    }
}