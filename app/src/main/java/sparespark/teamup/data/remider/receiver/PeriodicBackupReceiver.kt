package sparespark.teamup.data.remider.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import sparespark.teamup.core.START_ACTION_BACKUPS
import sparespark.teamup.data.service.BackupService

class PeriodicBackupReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null) return
        val serviceIntent = Intent(context, BackupService::class.java)
        serviceIntent.action = START_ACTION_BACKUPS
        context.startService(serviceIntent)
    }
}
