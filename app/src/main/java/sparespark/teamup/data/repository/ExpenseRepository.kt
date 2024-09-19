package sparespark.teamup.data.repository

import sparespark.teamup.core.wrapper.Result
import sparespark.teamup.data.model.expense.Expense

interface ExpenseRepository {
    suspend fun getExpenseList(localOnly: Unit? = null): Result<Exception, List<Expense>>
    suspend fun updateExpense(expense: Expense): Result<Exception, Unit>
    suspend fun deleteExpense(id: String): Result<Exception, Unit>
    suspend fun calculateTotalExpenses(list: List<Expense>): Result<Exception, Double>
    suspend fun clearListCacheTime(): Result<Exception, Unit>
}
