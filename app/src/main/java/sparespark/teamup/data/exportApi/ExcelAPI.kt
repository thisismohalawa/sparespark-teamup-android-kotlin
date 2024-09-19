package sparespark.teamup.data.exportApi

import sparespark.teamup.core.wrapper.Result
import sparespark.teamup.data.model.expense.Expense
import sparespark.teamup.data.model.item.Item


interface ExcelAPI {
    suspend fun buildItemsFile(list: List<Item>?): Result<Exception, Unit>
    suspend fun buildExpensesFile(list: List<Expense>?): Result<Exception, Unit>
}