package sparespark.teamup.core

import android.content.Context
import android.os.Handler
import android.os.Looper
import sparespark.teamup.R
import sparespark.teamup.data.exportApi.toActiveText
import sparespark.teamup.data.exportApi.toTypeText
import sparespark.teamup.data.model.item.Item
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

internal fun String?.isNewIdItem(): Boolean = this.isNullOrBlank()

internal fun Int?.isOwner(): Boolean = this?.equals(ROLE_OWNER) == true

internal fun Int?.isAdmin(): Boolean = this?.equals(ROLE_ADMIN) == true

internal fun Int.toNumString(): String = if (this < 0) "X" else this.toString()

internal fun String.plusQuan(context: Context?): String =
    this + " ${context?.getString(R.string.un)}"

internal fun String.plusCurrency(context: Context?): String =
    this + " ${context?.getString(R.string.le)}"

internal fun getSystemTimeMillis(): String = System.currentTimeMillis().toString()

internal fun isDeviceLanguageArabic(): Boolean = Locale.getDefault().language.equals("ar")

internal fun getCalendarDateTime(pattern: String = DATE_FULL_FORMAT): String = try {
    val cal = Calendar.getInstance(TimeZone.getDefault())
    val sdf = SimpleDateFormat(pattern, Locale.US)
    sdf.timeZone = cal.timeZone
    sdf.format(cal.time)
} catch (ex: Exception) {
    "00/00/0000"
}

internal fun getCalendarSearchDay(day: Int = 0): String = try {
    val sdf = SimpleDateFormat(DATE_SEARCH_FORMAT, Locale.US)
    val cal = Calendar.getInstance(TimeZone.getDefault())
    cal.add(Calendar.DATE, day)
    sdf.timeZone = cal.timeZone
    sdf.format(cal.time)
} catch (ex: Exception) {
    "00/00/0000"
}

internal fun handlerPostDelayed(millisValue: Long, action: (() -> Unit)? = null) {
    Handler(Looper.getMainLooper()).postDelayed({
        action?.let { it() }
    }, millisValue)
}

internal fun Int.toRoleTitle(context: Context?): String =
    if (context == null) "Team" else when (this) {
        ROLE_OWNER -> context.getString(R.string.owner)
        ROLE_ADMIN -> context.getString(R.string.admin)
        else -> context.getString(R.string.team)
    }

internal fun Item.toShareText(pricePadding: Boolean = true): String {
    val pLines = if (pricePadding) "\n\n" else "\n"
    return "Type: ${this.sell.toTypeText()}\n" + "Status: ${this.active.toActiveText()}$pLines" + "Price: ${this.assetEntry.assetPrice}\n" + "Quantity: ${this.assetEntry.quantity}\n" + "Total: ${
        getTotal(
            this.assetEntry.assetPrice, this.assetEntry.quantity
        ).toFormatedString()
    }$pLines" + "Date: ${this.creationDate}\n" + "Client: ${this.clientEntry.name}\n" + "City: ${this.clientEntry.city}\n" + "Note: ${this.note}."
}
