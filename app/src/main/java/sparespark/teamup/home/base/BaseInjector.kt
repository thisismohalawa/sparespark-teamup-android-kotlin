package sparespark.teamup.home.base

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import sparespark.teamup.data.implementation.CityRepositoryImpl
import sparespark.teamup.data.implementation.ClientRepositoryImpl
import sparespark.teamup.data.implementation.NoteRepositoryImpl
import sparespark.teamup.data.implementation.PreferenceRepositoryImpl
import sparespark.teamup.data.implementation.TeamRepositoryImpl
import sparespark.teamup.data.implementation.UserRepositoryImpl
import sparespark.teamup.data.network.connectivity.ConnectivityInterceptorImpl
import sparespark.teamup.data.preference.CityBasePreferenceImpl
import sparespark.teamup.data.preference.ClientBasePreferenceImpl
import sparespark.teamup.data.preference.NoteBasePreferenceImpl
import sparespark.teamup.data.preference.util.AdvanceBasePreferenceImpl
import sparespark.teamup.data.preference.util.CacheBasePreferenceImpl
import sparespark.teamup.data.preference.util.ListBasePreferenceImpl
import sparespark.teamup.data.room.TeamDatabase

open class BaseInjector(
    val app: Application
) : AndroidViewModel(app) {

    protected fun getCityRepository() = CityRepositoryImpl(
        local = getCityDao(),
        pref = getCityPref(),
        localUser = getUserDao(),
        listPref = getUtilListPref(),
        connectInterceptor = getConnectInterceptor()
    )

    protected fun getClientRepository() = ClientRepositoryImpl(
        local = getClientDao(),
        pref = getClientPref(),
        localUser = getUserDao(),
        listPref = getUtilListPref(),
        advancePref = getUtilAdvancePref(),
        connectInterceptor = getConnectInterceptor()
    )

    protected fun getNoteRepository() = NoteRepositoryImpl(
        local = getNoteDao(),
        pref = getNotePref(),
        localUser = getUserDao(),
        listPref = getUtilListPref(),
        advancePref = getUtilAdvancePref(),
        connectInterceptor = getConnectInterceptor()
    )


    protected fun getTeamRepository() = TeamRepositoryImpl(
        localUser = getUserDao(),
        listPref = getUtilListPref(),
        connectInterceptor = getConnectInterceptor()
    )

    protected fun getUserRepository() = UserRepositoryImpl(
        local = getUserDao(),
        listPref = getUtilListPref(),
        connectInterceptor = getConnectInterceptor()
    )

    protected fun getPreferenceRepository() = PreferenceRepositoryImpl(
        localDB = TeamDatabase.invoke(getApplication()),
        cachePref = getUtilCachePref(),
        local = getUserDao(),
        listPref = getUtilListPref(),
        advancePref = getUtilAdvancePref(),
        connectInterceptor = getConnectInterceptor()
    )


    protected fun getConnectInterceptor() = ConnectivityInterceptorImpl(getApplication())
    protected fun getUserDao() = TeamDatabase.invoke(getApplication()).userDao()
    protected fun getUtilListPref() = ListBasePreferenceImpl(getApplication())
    protected fun getUtilAdvancePref() = AdvanceBasePreferenceImpl(getApplication())

    private fun getCityDao() = TeamDatabase.invoke(getApplication()).cityDao()
    private fun getClientDao() = TeamDatabase.invoke(getApplication()).clientDao()
    private fun getNoteDao() = TeamDatabase.invoke(getApplication()).noteDao()

    private fun getCityPref() = CityBasePreferenceImpl(getApplication())
    private fun getClientPref() = ClientBasePreferenceImpl(getApplication())
    private fun getNotePref() = NoteBasePreferenceImpl(getApplication())

    private fun getUtilCachePref() = CacheBasePreferenceImpl(getApplication())
}