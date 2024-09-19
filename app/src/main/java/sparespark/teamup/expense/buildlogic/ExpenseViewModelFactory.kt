package sparespark.teamup.expense.buildlogic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import sparespark.teamup.data.repository.ClientRepository
import sparespark.teamup.data.repository.ExpenseRepository
import sparespark.teamup.data.repository.TeamRepository
import sparespark.teamup.expense.ExpenseViewModel

class ExpenseViewModelFactory(
    private val expenseRepo: ExpenseRepository,
    private val clientRepo: ClientRepository,
    private val teamRepo: TeamRepository
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        if (modelClass.isAssignableFrom(ExpenseViewModel::class.java))
            @Suppress("UNCHECKED_CAST")
            ExpenseViewModel(expenseRepo, clientRepo, teamRepo) as T
        else throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
}
