package sparespark.teamup.data.reminder.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import sparespark.teamup.core.ACTION_DATA_BACKUPS
import sparespark.teamup.data.reminder.service.BackupService

class PeriodicBackupReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null) return
        val serviceIntent = Intent(context, BackupService::class.java)
        serviceIntent.action = ACTION_DATA_BACKUPS
        context.startService(serviceIntent)
    }
}
