package sparespark.teamup.auh

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputLayout
import sparespark.teamup.R
import sparespark.teamup.core.MAX_PASS_DIG
import sparespark.teamup.core.MIN_PASS_DIG
import sparespark.teamup.core.beginLayoutTitleLengthWatcher
import sparespark.teamup.core.isEmailAddress
import sparespark.teamup.core.setEmailInput
import sparespark.teamup.core.setPasswordInput

abstract class BaseAuthView : Fragment() {

    protected fun EditText.setUserEmailInput(inputLayout: TextInputLayout) = try {
        this.apply {
            inputLayout.hint = getString(R.string.email)
            setEmailInput()
            beginLayoutEmailWatcher(
                inputLayout = inputLayout,
                eMsg = getString(R.string.invalid)
            )
        }
    } catch (ex: Exception) {
        ex.printStackTrace()
    }

    protected fun EditText.setUserPasswordInput(inputLayout: TextInputLayout) = try {
        this.apply {
            inputLayout.hint = getString(R.string.password)
            inputLayout.isPasswordVisibilityToggleEnabled = true
            setPasswordInput()
            beginLayoutTitleLengthWatcher(
                inputLayout = inputLayout,
                minDig = MIN_PASS_DIG,
                maxDig = MAX_PASS_DIG,
                eMsg = getString(R.string.invalid)
            )
        }
    } catch (ex: Exception) {
        ex.printStackTrace()
    }

    private fun EditText.beginLayoutEmailWatcher(inputLayout: TextInputLayout, eMsg: String) {
        this.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(s: Editable) {
            }

            override fun onTextChanged(
                text: CharSequence,
                start: Int,
                count: Int,
                after: Int
            ): Unit {
                if (text.isNotEmpty() && text.toString().isEmailAddress()) {
                    inputLayout.isErrorEnabled = false
                    inputLayout.error = null
                } else inputLayout.error = eMsg
            }
        })
    }
}
