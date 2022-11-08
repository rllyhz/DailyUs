package id.rllyhz.dailyus.presentation.view

import android.content.Context
import android.os.Build
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Patterns
import androidx.appcompat.widget.AppCompatEditText
import id.rllyhz.dailyus.R

class EmailEditText : AppCompatEditText {
    constructor(context: Context) : super(context) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initView()
    }

    private fun initView() {
        inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        compoundDrawablePadding = 10
        setHint(R.string.email_hint)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            setAutofillHints(AUTOFILL_HINT_EMAIL_ADDRESS)
        }

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {}

            override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
                error =
                    if (!text.isNullOrEmpty() && !Patterns.EMAIL_ADDRESS.matcher(text).matches()) {
                        resources.getString(R.string.email_invalid_message)
                    } else null
            }
        })
    }
}