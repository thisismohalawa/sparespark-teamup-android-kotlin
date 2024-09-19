package sparespark.teamup.data.remider

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import sparespark.teamup.core.wrapper.Result
import sparespark.teamup.data.remider.receiver.PeriodicBackupReceiver
import java.util.Calendar

private const val RC = 0

class ReminderAPIImpl(
    private val context: Context
) : ReminderAPI {

    override fun setupReminderAlarmForBackup(): Result<Exception, Unit> =
        setReminder()

    override fun cancelBackupReminder(): Result<Exception, Unit> =
        cancelReminder()


    private fun setReminder(): Result<Exception, Unit> = Result.build {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, PeriodicBackupReceiver::class.java)
        if (!intent.isScheduled()) {
            println("Backups: scheduling... ")
            val calendar = Calendar.getInstance()
            calendar[Calendar.HOUR_OF_DAY] = 0
            calendar[Calendar.MINUTE] = 0
            calendar[Calendar.SECOND] = 0
            val startUpTime = calendar.timeInMillis
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                RC,
                intent,
                PendingIntent.FLAG_IMMUTABLE
            )
            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                startUpTime, AlarmManager.INTERVAL_DAY, pendingIntent
            )
            println("Backups: scheduled done. ")

        } else
            println("Backups: scheduled already. ")


    }

    private fun cancelReminder(): Result<Exception, Unit> = Result.build {
        val intent = Intent(context, PeriodicBackupReceiver::class.java)
        if (intent.isScheduled()) {
            println("Backups: cancel reminder. ")
            val pendingIntent = PendingIntent.getBroadcast(
                context, RC,
                intent,
                PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
            )
            pendingIntent?.cancel()
        }
    }

    private fun Intent.isScheduled(): Boolean =
        PendingIntent.getBroadcast(
            context, RC,
            this,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        ) != null
}