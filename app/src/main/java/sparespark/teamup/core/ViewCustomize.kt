package sparespark.teamup.core

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.text.parseAsHtml
import sparespark.teamup.R
import java.util.Locale

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

internal fun MenuItem.setRedTitle() = try {
    val hexColor =
        Integer.toHexString(Color.parseColor("#c46e6e")).toUpperCase(Locale.ROOT).substring(2)
    val html = "<font color='#$hexColor'>$title</font>"
    this.title = html.parseAsHtml()
} catch (ex: Exception) {
    ex.printStackTrace()
}

internal fun TextView.setHintDrawable(
    @DrawableRes drawable: Int, isRevered: Boolean = false
) {
    var isAr = isDeviceLanguageArabic()
    if (isRevered) isAr = !isAr

    if (isAr) this.setCompoundDrawablesWithIntrinsicBounds(drawable, 0, 0, 0)
    else this.setCompoundDrawablesWithIntrinsicBounds(0, 0, drawable, 0)
}

internal fun TextView.setDrawablePNType(
    income: Boolean, context: Context
) = try {
    fun setTintColor(
        @ColorRes color: Int, context: Context
    ) {
        if (isDeviceLanguageArabic()) this.compoundDrawables[2].setTint(
            ContextCompat.getColor(
                context,
                color
            )
        )
        else this.compoundDrawables[0].setTint(ContextCompat.getColor(context, color))
    }

    if (income) {
        setHintDrawable(R.drawable.ic_arrow_up, isRevered = true)
        setTintColor(R.color.green, context)
    } else {
        setHintDrawable(R.drawable.ic_arrow_down, isRevered = true)
        setTintColor(R.color.red, context)
    }

} catch (ex: Exception) {
    ex.printStackTrace()
}

internal fun ViewGroup.onViewedClickUpdateExpanding(
    expandingLayout: ViewGroup
) {
    if (expandingLayout.visibility != View.VISIBLE) {
        TransitionManager.beginDelayedTransition(this, AutoTransition())
        expandingLayout.visible(true)
    }
}