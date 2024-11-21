package sparespark.teamup.data.reminder.service

import android.app.Notification
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import sparespark.teamup.core.ACTION_DATA_BACKUPS
import sparespark.teamup.core.STOP_SERVICE
import sparespark.teamup.core.getCalendarSearchDay
import sparespark.teamup.core.internal.toTransaction
import sparespark.teamup.core.lazyDeferred
import sparespark.teamup.core.wrapper.Result
import sparespark.teamup.data.exportApi.ExcelAPIImpl
import sparespark.teamup.data.implementation.TransactionRepositoryImpl
import sparespark.teamup.data.model.transaction.RemoteTransaction
import sparespark.teamup.data.network.connectivity.ConnectivityInterceptorImpl
import sparespark.teamup.data.preference.TransactionPreferenceImpl
import sparespark.teamup.data.preference.util.UtilPreferenceImpl
import sparespark.teamup.data.reminder.notification.NotificationManager
import sparespark.teamup.data.room.TeamDatabase

class BackupService : Service() {

    private val jobTracker = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    override fun onCreate() {
        super.onCreate()

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O)
            startForeground(2, NotificationManager.startForegroundServiceNotification(this))
        else startForeground(1, Notification())
    }

    override fun onDestroy() {
        jobTracker.cancel()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        fun stopService() {
            stopForeground(true)
            stopSelfResult(startId)
        }
        when (intent?.action) {
            ACTION_DATA_BACKUPS -> coroutineScope.launch {
                fun getTransactionDao() = TeamDatabase.invoke(application).transactionDao()
                fun getItemPref() = TransactionPreferenceImpl(application)
                fun getUserDao() = TeamDatabase.invoke(application).userDao()
                fun getUtilPref() = UtilPreferenceImpl(application)
                fun getConnectInterceptor() = ConnectivityInterceptorImpl(application)

                val deferredTransactionList by lazyDeferred {
                    when (val result = TransactionRepositoryImpl(
                        local = getTransactionDao(),
                        pref = getItemPref(),
                        filterPreference = null,
                        balancePreference = null,
                        localUser = getUserDao(),
                        utilPreference = getUtilPref(),
                        connectInterceptor = getConnectInterceptor(),
                    ).filterItemList(
                        item = RemoteTransaction(
                            creationDate = getCalendarSearchDay(day = -1)
                        ).toTransaction
                    )) {
                        is Result.Error -> {
                            println("Backups: exporting Result error ${result.error}")
                            emptyList()
                        }

                        is Result.Value -> {
                            println("Backups: exporting..size=${result.value.size}")
                            result.value
                        }
                    }
                }

                when (val result = ExcelAPIImpl().buildTransactionListFile(
                    list = deferredTransactionList.await()
                )) {
                    is Result.Error -> {
                        println("Backups: exporting Result error ${result.error}")
                        stopService()
                    }

                    is Result.Value -> {
                        println("Backups: completed.")
                        stopService()
                    }
                }

            }

            STOP_SERVICE -> {
                stopService()
            }
        }
        return START_STICKY
    }
}