package id.rllyhz.dailyus.utils

import android.content.Context
import android.view.View
import android.widget.Button
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import id.rllyhz.dailyus.R

fun showAuthSnackBar(
    context: Context,
    view: View,
    submitButton: MaterialButton,
    message: String
) {
    Snackbar.make(
        view,
        message,
        Snackbar.LENGTH_LONG
    ).apply {
        anchorView = submitButton
        setBackgroundTint(
            ContextCompat.getColor(context, R.color.my_orange)
        )

    }.show()
}

fun showPostSnackBar(
    context: Context,
    view: View,
    submitButton: MaterialButton,
    message: String
) {
    Snackbar.make(
        view,
        message,
        Snackbar.LENGTH_LONG
    ).apply {
        anchorView = submitButton

        setBackgroundTint(
            ContextCompat.getColor(context, R.color.my_purple_500)
        )
        setTextColor(
            ContextCompat.getColor(context, R.color.white)
        )

    }.show()
}

fun View.show() {
    alpha = 1f
    visibility = View.VISIBLE
}

fun View.hide() {
    alpha = 0f
    visibility = View.GONE
}

fun Button.clickable() {
    isClickable = true
    isEnabled = true
}

fun Button.notClickable() {
    isClickable = false
    isEnabled = false
}