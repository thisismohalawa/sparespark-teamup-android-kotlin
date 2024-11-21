package sparespark.teamup.core

import android.content.Context
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Patterns
import android.widget.EditText
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.preference.EditTextPreference
import com.google.android.material.textfield.TextInputLayout
import sparespark.teamup.R
import java.util.Locale

internal const val MIN_PASS_DIG = 6
internal const val MAX_PASS_DIG = 20

internal const val MAX_ASSET_DIG = 8
internal const val MAX_ASSET_PRICE_DIG = 8
internal const val MAX_ASSET_TOTAL_PRICE_DIG = 16

internal const val MIN_INPUT_NAME_DIG = 6
internal const val MAX_INPUT_NAME_DIG = 30

internal const val MIN_INPUT_TITLE_DIG = 3
internal const val MAX_INPUT_TITLE_DIG = 60
internal const val MAX_INPUT_NOTE_DIG = 120

internal const val NUMBER_DEC_TYPE =
    InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL

internal fun String.isEmailAddress(): Boolean = Patterns.EMAIL_ADDRESS.matcher(this).matches()
internal fun String.isPhoneNumberValid(): Boolean = Patterns.PHONE.matcher(this).matches()
internal fun String.isValidPasswordLength(): Boolean = length in MIN_PASS_DIG..MAX_PASS_DIG
internal fun String?.isNewStringItem(): Boolean = this.isNullOrBlank()
internal fun String.isMatch(query: String?): Boolean =
    query?.lowercase(Locale.ROOT)?.let { this.lowercase(Locale.ROOT).contains(it) } == true

internal fun EditText.setEmailInput() {
    this.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
}

internal fun EditText.setPasswordInput() {
    this.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
}

internal fun EditText.setNumberDecimalInput() {
    this.inputType = NUMBER_DEC_TYPE
}

internal fun EditText.setPhoneNumInput() {
    this.inputType = InputType.TYPE_CLASS_PHONE
}


internal fun EditTextPreference.setNumberDecimalPrefInput() {
    setOnBindEditTextListener { editText ->
        editText.inputType = NUMBER_DEC_TYPE
    }
}

internal fun EditText.beginLayoutTitleLengthWatcher(
    inputLayout: TextInputLayout,
    minDig: Int,
    maxDig: Int,
    eMsg: String
) {
    this.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun afterTextChanged(s: Editable) {
        }

        override fun onTextChanged(text: CharSequence, start: Int, count: Int, after: Int): Unit {
            if (text.isNotEmpty() &&
                text.toString().length in minDig..maxDig
            ) {
                inputLayout.isErrorEnabled = false
                inputLayout.error = null
            } else inputLayout.error = eMsg
        }
    })
}

internal fun Context.displayConfirmDialog(
    @StringRes title: Int,
    @StringRes positive: Int = R.string.confirm,
    msg: String? = null,
    pAction: (() -> Unit)?
) {
    AlertDialog.Builder(this@displayConfirmDialog)
        .setTitle(title)
        .setMessage(msg)
        .setPositiveButton(this.getString(positive)) { _, _ ->
            pAction?.let { it() }
        }.show()
}
