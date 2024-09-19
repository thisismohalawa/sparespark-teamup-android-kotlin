package sparespark.teamup.expense.buildlogic

import android.app.Application
import sparespark.teamup.data.implementation.ExpenseRepositoryImpl
import sparespark.teamup.data.preference.ExpenseBasePreferenceImpl
import sparespark.teamup.data.room.TeamDatabase
import sparespark.teamup.home.base.BaseInjector

open class ExpenseInjector(
    app: Application
) : BaseInjector(app) {

    private fun getExpenseDao() = TeamDatabase.invoke(getApplication()).expenseDao()

    private fun getExpensePref() = ExpenseBasePreferenceImpl(getApplication())

    private fun getExpenseRepository() = ExpenseRepositoryImpl(
        local = getExpenseDao(),
        pref = getExpensePref(),
        localUser = getUserDao(),
        listPref = getUtilListPref(),
        connectInterceptor = getConnectInterceptor()
    )

    fun provideViewModelFactory() = ExpenseViewModelFactory(
        getExpenseRepository(), getClientRepository(), getTeamRepository()
    )
}
