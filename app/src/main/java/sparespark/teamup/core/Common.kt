package sparespark.teamup.core

import android.os.Handler
import android.os.Looper
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

internal const val DATE_SEARCH_FORMAT = "dd/MM/yyyy"
internal const val MONTH_YEAR_FORMAT = "MM/yyyy"
internal const val DATE_FULL_FORMAT = "dd/MM/yyyy hh:mm:ss a"


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

internal fun toCalendarSearchDay(cal: Long): String = try {
    val sdf = SimpleDateFormat(DATE_SEARCH_FORMAT, Locale.US)
    sdf.format(cal)
} catch (ex: Exception) {
    "00/00/0000"
}

internal fun handlerPostDelayed(millisValue: Long, action: (() -> Unit)? = null) {
    Handler(Looper.getMainLooper()).postDelayed({
        action?.let { it() }
    }, millisValue)
}
