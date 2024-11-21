package sparespark.teamup.data.reminder

import sparespark.teamup.core.wrapper.Result


interface ReminderAPI {
    fun setupReminderAlarmForBackup(): Result<Exception, Unit>
    fun cancelBackupReminder(): Result<Exception, Unit>
}
