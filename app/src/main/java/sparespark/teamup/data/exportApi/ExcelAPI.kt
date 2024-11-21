package sparespark.teamup.data.exportApi

import sparespark.teamup.core.wrapper.Result
import sparespark.teamup.data.model.stock.Stock
import sparespark.teamup.data.model.transaction.Transaction


interface ExcelAPI {
    suspend fun buildTransactionListFile(list: List<Transaction>): Result<Exception, Unit>
    suspend fun buildStockListFile(list: List<Stock>): Result<Exception, Unit>
}