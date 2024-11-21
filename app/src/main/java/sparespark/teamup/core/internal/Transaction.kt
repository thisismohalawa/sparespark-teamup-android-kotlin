package sparespark.teamup.core.internal

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import com.google.android.material.textfield.TextInputLayout
import sparespark.teamup.R
import sparespark.teamup.data.model.AssetEntry
import sparespark.teamup.data.model.ClientEntry
import sparespark.teamup.data.model.transaction.Transaction
import java.util.Locale
import kotlin.math.roundToInt


internal fun String.plusQuan(context: Context?): String =
    this + " ${context?.getString(R.string.un)}"

internal fun String.plusCurrency(context: Context?): String =
    this + " ${context?.getString(R.string.le)}"

internal fun String.plusAverage(context: Context?): String =
    this + " ${context?.getString(R.string.avr)}"

internal fun Boolean.toActiveText(): String = if (this) "Active/غير مكتمل" else "Inactive/مكتمل"
internal fun Boolean.toTypeText(): String = if (this) "Sell/بيع" else "Buy/شراء"
internal fun Double.toFormatedString(): String = try {
    if (this > Int.MAX_VALUE) "MAX"
    else String.format(Locale.US, "%,.2f", this@toFormatedString)
} catch (e: Exception) {
    Double.toString()
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

internal fun Transaction.toShareText(pricePadding: Boolean = true): String {
    val pLines = if (pricePadding) "\n\n" else "\n"
    return "Type: ${this.sell.toTypeText()}\n" +
            "Status: ${this.active.toActiveText()}$pLines" +
            "Price: ${this.assetEntry.assetPrice}\n" +
            "Quantity: ${this.assetEntry.quantity}\n" +
            "Total: ${
                getTotal(
                    this.assetEntry.assetPrice,
                    this.assetEntry.quantity
                ).toFormatedString()
            }$pLines" +
            "Date: ${this.creationDate}\n" +
            "Client: ${this.clientEntry.name}\n" +
            "City: ${this.clientEntry.city}\n" +
            "Note: ${this.note}."
}

internal fun newTransactionItem() = Transaction(
    "",
    "",
    ClientEntry(),
    AssetEntry(),
    active = true,
    temp = false,
    sell = true,
    note = "",
    updateBy = "",
    updateDate = ""
)

internal fun EditText.beginInputLayoutAssetWatcher(
    inputLayout: TextInputLayout,
    maxDig: Int,
    invalidMsg: String
) {
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
        ) {
            if (text.isNotEmpty()) if (text.toString().isValidNumFormated(maxDig)) {
                inputLayout.isErrorEnabled = false
                inputLayout.error = null
            } else inputLayout.error = invalidMsg
        }
    })
}