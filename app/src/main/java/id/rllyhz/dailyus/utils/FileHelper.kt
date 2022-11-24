package id.rllyhz.dailyus.utils

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Environment
import id.rllyhz.dailyus.R
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*

fun File.getBitmap(isBackCameraMode: Boolean): Bitmap {
    val matrix = Matrix()
    val bitmap = BitmapFactory.decodeFile(absolutePath)

    matrix.run {
        if (isBackCameraMode) {
            postRotate(90f)
        } else {
            postRotate(-90f)
            postScale(-1f, 1f, (bitmap.width / 2).toFloat(), (bitmap.height / 2).toFloat())
        }
    }

    return Bitmap.createBitmap(
        bitmap,
        0, 0,
        bitmap.width, bitmap.height,
        matrix,
        true
    )
}

fun File.getCompressedImageFile(): File {
    val bitmap = BitmapFactory.decodeFile(path)
    var compressQuality = 100
    var streamLength: Int

    do {
        val bmStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmStream)
        val bmpPicByteArray = bmStream.toByteArray()
        streamLength = bmpPicByteArray.size
        compressQuality -= 5
    } while (streamLength > 1000000)

    bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, FileOutputStream(this))

    return this
}

val File.size get(): Double = if (exists()) length().toDouble() else 0.0
val File.sizeInKb get() = size / 1024
val File.sizeInMb get() = sizeInKb / 1024
val Double.formattedSize get() = String.format("%.2f", this).toDouble()

fun Uri.toFile(context: Context): File {
    val contentResolver = context.contentResolver
    val file = createCustomTempFile(context)
    val inputStream = contentResolver.openInputStream(this) as InputStream
    val outStream = FileOutputStream(file)
    val buffer = ByteArray(1024)
    var size: Int

    while (inputStream.read(buffer).also { size = it } > 0) outStream.write(buffer, 0, size)

    outStream.close()
    inputStream.close()

    return file
}

fun createImageFile(application: Application): File {
    val mediaDirectory = application.externalMediaDirs.firstOrNull()?.let {
        File(
            it,
            application.resources.getString(R.string.temp_file_name)
        ).apply { mkdirs() }
    }

    val outputDirectory =
        if (mediaDirectory != null && mediaDirectory.exists()) mediaDirectory else application.filesDir

    return File(outputDirectory, createTimestamp() + ".jpg")
}


private fun createCustomTempFile(context: Context): File {
    val storageDirectory = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    return File.createTempFile(createTimestamp(), ".jpg", storageDirectory)
}

private fun createTimestamp() = SimpleDateFormat(
    "dd-MMM-yyyy",
    Locale.US
).format(System.currentTimeMillis())