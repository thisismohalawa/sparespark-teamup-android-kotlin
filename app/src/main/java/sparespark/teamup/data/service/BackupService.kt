package sparespark.teamup.data.service

import android.app.Notification
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.widget.Toast
import androidx.annotation.StringRes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import sparespark.teamup.R
import sparespark.teamup.core.START_ACTION_BACKUPS
import sparespark.teamup.core.START_EXPENSE_EXPORT
import sparespark.teamup.core.START_ITEM_EXPORT
import sparespark.teamup.core.STOP_SERVICE
import sparespark.teamup.core.getCalendarSearchDay
import sparespark.teamup.core.lazyDeferred
import sparespark.teamup.core.map.toList
import sparespark.teamup.core.map.toListItemX
import sparespark.teamup.core.wrapper.Result
import sparespark.teamup.data.exportApi.ExcelAPIImpl
import sparespark.teamup.data.implementation.ItemRepositoryImpl
import sparespark.teamup.data.network.connectivity.ConnectivityInterceptorImpl
import sparespark.teamup.data.preference.ItemBasePreferenceImpl
import sparespark.teamup.data.preference.util.ListBasePreferenceImpl
import sparespark.teamup.data.remider.notify.NotificationManager
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
        fun displayToast(@StringRes res: Int) {
            Toast.makeText(
                this@BackupService,
                getString(res),
                Toast.LENGTH_SHORT
            ).show()
        }

        fun stopService() {
            stopForeground(true)
            stopSelfResult(startId)
        }

        when (intent?.action) {
            START_ITEM_EXPORT -> coroutineScope.launch {
                val deferredList by lazyDeferred {
                    TeamDatabase.invoke(application).itemDao().getList().toListItemX()
                }
                when (val result = ExcelAPIImpl().buildItemsFile(list = deferredList.await())) {
                    is Result.Error -> {
                        println("Backups: exporting Result error ${result.error}")
                        stopService()
                        displayToast(R.string.erro_data_backup)
                    }

                    is Result.Value -> {
                        println("Backups: completed.")
                        displayToast(R.string.export_success)
                        stopService()
                    }
                }
            }

            START_EXPENSE_EXPORT -> coroutineScope.launch {
                val deferredList by lazyDeferred {
                    TeamDatabase.invoke(application).expenseDao().getList().toList()
                }

                when (val result = ExcelAPIImpl().buildExpensesFile(list = deferredList.await())) {
                    is Result.Error -> {
                        println("Backups: exporting Result error ${result.error}")
                        stopService()
                        displayToast(R.string.erro_data_backup)
                    }

                    is Result.Value -> {
                        println("Backups: completed.")
                        displayToast(R.string.export_success)
                        stopService()
                    }
                }
            }

            START_ACTION_BACKUPS -> coroutineScope.launch {
                fun getItemDao() = TeamDatabase.invoke(application).itemDao()
                fun getUserDao() = TeamDatabase.invoke(application).userDao()
                fun getItemPref() = ItemBasePreferenceImpl(application)
                fun getListPref() = ListBasePreferenceImpl(application)
                fun getConnectInterceptor() = ConnectivityInterceptorImpl(application)

                val deferredList by lazyDeferred {
                    when (val result = ItemRepositoryImpl(
                        local = getItemDao(),
                        pref = getItemPref(),
                        localUser = getUserDao(),
                        listPref = getListPref(),
                        advancePref = null,
                        connectInterceptor = getConnectInterceptor()
                    ).getRemoteItemListByQuery(getCalendarSearchDay(day = -1))) {
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
                when (val result = ExcelAPIImpl().buildItemsFile(list = deferredList.await())) {
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
