package sparespark.teamup.core

import android.content.Context
import android.graphics.Color
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.text.parseAsHtml
import com.google.android.material.snackbar.Snackbar
import sparespark.teamup.R
import sparespark.teamup.data.model.client.Client
import sparespark.teamup.data.model.team.Team
import java.util.Locale

internal fun TextView.setHintDrawable(
    @DrawableRes drawable: Int, isRevered: Boolean = false
) {
    var isAr = isDeviceLanguageArabic()
    if (isRevered) isAr = !isAr

    if (isAr) this.setCompoundDrawablesWithIntrinsicBounds(drawable, 0, 0, 0)
    else this.setCompoundDrawablesWithIntrinsicBounds(0, 0, drawable, 0)
}

internal fun TextView.setDrawablePNType(
    total: Double, context: Context
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

    if (total > 0) {
        setHintDrawable(R.drawable.ic_arrow_up, isRevered = true)
        setTintColor(R.color.green, context)
    } else {
        setHintDrawable(R.drawable.ic_arrow_down, isRevered = true)
        setTintColor(R.color.red, context)
    }

} catch (ex: Exception) {
    ex.printStackTrace()
}

internal fun MenuItem.setRedTitle() = try {
    val hexColor =
        Integer.toHexString(Color.parseColor("#c46e6e")).toUpperCase(Locale.ROOT).substring(2)
    val html = "<font color='#$hexColor'>$title</font>"
    this.title = html.parseAsHtml()
} catch (ex: Exception) {
    ex.printStackTrace()
}

internal fun View.showSnackBar(
    msg: String,
    duration: Int,
    aMsg: String?,
    action: (() -> Unit)?
) {
    Snackbar.make(this@showSnackBar, msg, duration).apply {
        if (aMsg != null) setAction(aMsg) {
            action?.let { it() }
        }
        show()
    }
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

internal fun AutoCompleteTextView.bindClients(
    list: List<Client>,
    action: ((String) -> Unit)? = null
) {
    if (list.isEmpty()) {
        this.setAdapter(null)
        return
    }
    val clientNames = mutableListOf<String>()
    for (i in list.indices) clientNames.add(list[i].name)

    val aAdapter: ArrayAdapter<String> = ArrayAdapter(
        context, android.R.layout.simple_spinner_dropdown_item, clientNames
    )
    this.setAdapter(aAdapter)
    this.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
        action?.invoke(
            adapter.getItem(position).toString()
        )
    }
}

internal fun AutoCompleteTextView.bindTeam(
    list: List<Team>,
    action: ((String) -> Unit)? = null
) {
    if (list.isEmpty()) {
        this.setAdapter(null)
        return
    }

    val clientNames = mutableListOf<String>()
    for (i in list.indices) clientNames.add(list[i].name)

    val aAdapter: ArrayAdapter<String> = ArrayAdapter(
        context, android.R.layout.simple_spinner_dropdown_item, clientNames
    )
    this.setAdapter(aAdapter)
    this.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
        action?.invoke(
            adapter.getItem(position).toString()
        )
    }
}

internal fun bindDateWithInfo(
    dHint: String,
    iHint: String,
    date: String,
    info: String,
): String = "$dHint: $date" + if (info.isNotBlank()) "\n$iHint: $info."
else date + if (info.isNotBlank())
    "\n$iHint $info." else ""

internal fun bindNote(
    nHint: String,
    note: String
): String =
    if (note.isNotBlank()) "$nHint: $note"
    else ""