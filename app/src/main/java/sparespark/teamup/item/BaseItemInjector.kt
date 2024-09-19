package sparespark.teamup.item

import android.app.Application
import sparespark.teamup.data.implementation.ItemRepositoryImpl
import sparespark.teamup.data.implementation.StaticsRepositoryImpl
import sparespark.teamup.data.preference.ItemBasePreferenceImpl
import sparespark.teamup.data.preference.selector.LocalSelectorRepositoryImpl
import sparespark.teamup.data.preference.statics.CalenderStaticsPreferenceImpl
import sparespark.teamup.data.preference.statics.ListStaticsBasePreferenceImpl
import sparespark.teamup.data.room.TeamDatabase
import sparespark.teamup.home.base.BaseInjector

open class BaseItemInjector(
    app: Application
) : BaseInjector(app) {

    private fun getItemDao() = TeamDatabase.invoke(getApplication()).itemDao()

    private fun getItemPref() = ItemBasePreferenceImpl(getApplication())

    private fun getLStaticsPref() = ListStaticsBasePreferenceImpl(getApplication())

    private fun getCStaticsPref() = CalenderStaticsPreferenceImpl(getApplication())

    protected fun getSelectorRepository() = LocalSelectorRepositoryImpl(getApplication())

    protected fun getItemRepository() = ItemRepositoryImpl(
        local = getItemDao(),
        pref = getItemPref(),
        localUser = getUserDao(),
        listPref = getUtilListPref(),
        advancePref = getUtilAdvancePref(),
        connectInterceptor = getConnectInterceptor()
    )

    protected fun getStaticsRepository() = StaticsRepositoryImpl(
        lStaticsPref = getLStaticsPref(),
        cStaticsPref = getCStaticsPref(),
        advancePref = getUtilAdvancePref(),
        localUser = getUserDao(),
        listPref = getUtilListPref(),
        connectInterceptor = getConnectInterceptor()
    )
}