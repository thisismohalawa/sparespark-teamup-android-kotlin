package sparespark.teamup.core.base

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import sparespark.teamup.data.implementation.CityRepositoryImpl
import sparespark.teamup.data.implementation.ClientRepositoryImpl
import sparespark.teamup.data.implementation.CompanyRepositoryImpl
import sparespark.teamup.data.implementation.NoteRepositoryImpl
import sparespark.teamup.data.implementation.PreferenceRepositoryImpl
import sparespark.teamup.data.implementation.ProductRepositoryImpl
import sparespark.teamup.data.implementation.TeamRepositoryImpl
import sparespark.teamup.data.implementation.UserRepositoryImpl
import sparespark.teamup.data.network.connectivity.ConnectivityInterceptorImpl
import sparespark.teamup.data.preference.CityPreferenceImpl
import sparespark.teamup.data.preference.ClientPreferenceImpl
import sparespark.teamup.data.preference.CompanyPreferenceImpl
import sparespark.teamup.data.preference.NotePreferenceImpl
import sparespark.teamup.data.preference.ProductPreferenceImpl
import sparespark.teamup.data.preference.advance.AdvancedPreferenceImpl
import sparespark.teamup.data.preference.cache.CachePreferenceImpl
import sparespark.teamup.data.preference.util.UtilPreferenceImpl
import sparespark.teamup.data.room.TeamDatabase

open class BaseInjector(
    val app: Application
) : AndroidViewModel(app) {

    protected fun getUserDao() = TeamDatabase.invoke(getApplication()).userDao()
    private fun getCityDao() = TeamDatabase.invoke(getApplication()).cityDao()
    private fun getClientDao() = TeamDatabase.invoke(getApplication()).clientDao()
    private fun getCompanyDao() = TeamDatabase.invoke(getApplication()).companyDao()
    private fun getProductDao() = TeamDatabase.invoke(getApplication()).productDao()
    private fun getNoteDao() = TeamDatabase.invoke(getApplication()).noteDao()

    private fun getCityPref() = CityPreferenceImpl(getApplication())
    private fun getClientPref() = ClientPreferenceImpl(getApplication())
    private fun getCompanyPref() = CompanyPreferenceImpl(getApplication())
    private fun getNotePref() = NotePreferenceImpl(getApplication())
    private fun getProductPref() = ProductPreferenceImpl(getApplication())

    protected fun getUtilPreference() = UtilPreferenceImpl(getApplication())
    private fun getCachePreference() = CachePreferenceImpl(getApplication())
    private fun getAdvancedCachePreference() = AdvancedPreferenceImpl(getApplication())

    protected fun getConnectInterceptor() = ConnectivityInterceptorImpl(getApplication())

    protected fun getUserRepository() = UserRepositoryImpl(
        local = getUserDao(),
        utilPreference = getUtilPreference(),
        connectInterceptor = getConnectInterceptor()
    )

    protected fun getPreferenceRepository() = PreferenceRepositoryImpl(
        localDB = TeamDatabase.invoke(getApplication()),
        utilPreference = getUtilPreference(),
        cachePreference = getCachePreference(),
        advancedPreference = getAdvancedCachePreference()
    )

    protected fun getCityRepository() = CityRepositoryImpl(
        local = getCityDao(),
        pref = getCityPref(),
        localUser = getUserDao(),
        utilPreference = getUtilPreference(),
        connectInterceptor = getConnectInterceptor()
    )

    protected fun getClientRepository() = ClientRepositoryImpl(
        local = getClientDao(),
        pref = getClientPref(),
        localUser = getUserDao(),
        utilPreference = getUtilPreference(),
        connectInterceptor = getConnectInterceptor()
    )

    protected fun getCompanyRepository() = CompanyRepositoryImpl(
        local = getCompanyDao(),
        pref = getCompanyPref(),
        localUser = getUserDao(),
        utilPreference = getUtilPreference(),
        connectInterceptor = getConnectInterceptor()
    )

    protected fun getProductRepository() = ProductRepositoryImpl(
        local = getProductDao(),
        pref = getProductPref(),
        localUser = getUserDao(),
        utilPreference = getUtilPreference(),
        connectInterceptor = getConnectInterceptor()
    )

    protected fun getNoteRepository() = NoteRepositoryImpl(
        local = getNoteDao(),
        pref = getNotePref(),
        localUser = getUserDao(),
        utilPreference = getUtilPreference(),
        connectInterceptor = getConnectInterceptor()
    )

    protected fun getTeamRepository() = TeamRepositoryImpl(
        localUser = getUserDao(),
        utilPreference = getUtilPreference(),
        connectInterceptor = getConnectInterceptor()
    )
}