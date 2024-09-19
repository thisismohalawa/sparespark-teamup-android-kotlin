package sparespark.teamup.data.remider.notify

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import sparespark.teamup.R
import sparespark.teamup.home.HomeActivity

object NotificationManager {

    // FOREGROUND
    private const val FORE_NOTIFICATION_CHANNEL_ID = "teamup.services"
    private const val FORE_CHANNEL_NAME = "Teamup Services"

    fun startForegroundServiceNotification(context: Context): Notification? {
        val nChannel = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel(
                FORE_NOTIFICATION_CHANNEL_ID, FORE_CHANNEL_NAME, NotificationManager.IMPORTANCE_NONE
            )
        } else return null

        val manager =
            (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)

        val nBuilder = NotificationCompat.Builder(context, FORE_NOTIFICATION_CHANNEL_ID)

        manager.createNotificationChannel(nChannel)

        return nBuilder.setOngoing(true).setContentTitle(context.getString(R.string.data_backup))
            .setContentText(context.getString(R.string.start_backup))
            .setContentIntent(getPendingIntentWithStack(context, HomeActivity::class.java))
            .setPriority(NotificationManager.IMPORTANCE_MIN).setSmallIcon(R.drawable.ic_buy)
            .setCategory(Notification.CATEGORY_SERVICE).build()
    }

    private fun <T> getPendingIntentWithStack(
        context: Context, javaClass: Class<T>
    ): PendingIntent {
        val resultIntent = Intent(context, javaClass)
        resultIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP

        val stackBuilder = TaskStackBuilder.create(context)
        stackBuilder.addParentStack(javaClass)
        stackBuilder.addNextIntent(resultIntent)

        return stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
    }
}
