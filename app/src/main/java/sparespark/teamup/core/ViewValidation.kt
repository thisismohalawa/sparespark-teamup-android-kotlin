package sparespark.teamup.core

import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Patterns
import android.widget.EditText
import androidx.preference.EditTextPreference
import com.google.android.material.textfield.TextInputLayout
import java.util.Locale
import kotlin.math.roundToInt

internal const val NUMBER_DEC_TYPE =
    InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL

internal fun String.isMatch(query: String): Boolean =
    this.lowercase(Locale.ROOT).contains(query.lowercase(Locale.ROOT))

internal fun String.isEmailAddress(): Boolean = Patterns.EMAIL_ADDRESS.matcher(this).matches()

internal fun String.isValidPasswordLength(): Boolean = length in MIN_PASS_DIG..MAX_PASS_DIG


internal fun String.isValidLength(maxDig: Int): Boolean =
    length in MIN_TITLE_DIG..maxDig

internal fun String.isPhoneNumberValid(): Boolean =
    Patterns.PHONE.matcher(this).matches()

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

internal fun Double.toFormatedString(): String = try {
    if (this > Int.MAX_VALUE) "MAX"
    else String.format(Locale.US, "%,.2f", this@toFormatedString)
} catch (e: Exception) {
    Double.toString()
}

internal fun String.isValidNumFormated(maxDig: Int): Boolean = try {
    !(this.length !in 1..maxDig ||
            this == "0.0" ||
            this == "0" ||
            this == "." ||
            this.isBlank() ||
            this.contains("E") ||
            this.startsWith("00") ||
            this.startsWith(".") ||
            this.endsWith("."))
} catch (ex: Exception) {
    false
}

internal fun Double.limitDouble(): Double = try {
    (this * 100.0).roundToInt() / 100.0
} catch (e: Exception) {
    0.0
}

internal fun getTotal(assetPrice: Double?, quantity: Double?): Double = try {
    ((assetPrice ?: 0.0) * (quantity ?: 0.0)).limitDouble()
} catch (ex: Exception) {
    0.0
}

internal fun getQuantity(assetPrice: Double?, total: Double?): Double = try {
    ((total ?: 0.0) / (assetPrice ?: 0.0)).limitDouble()
} catch (e: Exception) {
    0.0
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