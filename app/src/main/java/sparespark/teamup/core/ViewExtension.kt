package sparespark.teamup.core

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.text.Editable
import android.text.InputType
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.preference.EditTextPreference
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputLayout
import sparespark.teamup.R

internal fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)

internal fun Activity.makeToast(msg: String) = Toast.makeText(
    this@makeToast, msg, Toast.LENGTH_SHORT
).show()

internal fun View.enable(enabled: Boolean) {
    isEnabled = enabled
    alpha = if (enabled) 1f else 0.5f
}

internal fun View.visible(isVisible: Boolean) {
    visibility = if (isVisible) View.VISIBLE else View.GONE
}

internal fun ImageView.setCustomImage(@DrawableRes drawable: Int, context: Context?) =
    if (context != null) setImageDrawable(ContextCompat.getDrawable(context, drawable))
    else Unit

internal fun TextView.setCustomColor(@ColorRes intColor: Int, context: Context?) =
    if (context != null) setTextColor(ContextCompat.getColor(context, intColor))
    else setTextColor(Color.DKGRAY)

internal fun TextView.setCustomIcon(
    isIncome: Boolean, @DrawableRes inDrawable: Int, @DrawableRes outDrawable: Int
) {
    if (isIncome) this.setHintDrawable(inDrawable)
    else this.setHintDrawable(outDrawable)
}



internal fun TextView.setLabeled(labeled: Boolean) {
    this.paintFlags = if (labeled) paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
    else this.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
}

internal fun androidx.appcompat.widget.Toolbar.setToolbarTitleFont() = try {
    for (i in 0 until childCount) {
        val view = getChildAt(i)
        if (view is TextView && view.text == title) {
            view.typeface = ResourcesCompat.getFont(context, R.font.tango_bold)
            break
        }
    }
} catch (ex: Exception) {
    ex.printStackTrace()
}

internal fun RecyclerView.setupListItemDecoration(context: Context) {
    addItemDecoration(
        DividerItemDecoration(
            context, DividerItemDecoration.VERTICAL
        )
    )
}

internal fun TextInputLayout.setIconAction(
    icon: Int, action: (() -> Unit)?
) {
    setEndIconActivated(true)
    isEndIconVisible = true
    setEndIconDrawable(icon)
    setEndIconOnClickListener {
        action?.invoke()
    }
}
