package sparespark.teamup.data.repository

import sparespark.teamup.core.wrapper.Result
import sparespark.teamup.data.model.balance.TransactionBalance
import sparespark.teamup.data.model.statics.TransactionCalendarStatics
import sparespark.teamup.data.model.transaction.Transaction

interface TransactionRepository {
    suspend fun getItemListHintTitle(): Result<Exception, List<Int>>
    suspend fun getFilteredItemListHintTitle(): Result<Exception, List<Int>>
    suspend fun getBalanceListHintTitle(): Result<Exception, List<Int>>

    suspend fun getItemById(itemId: String): Result<Exception, Transaction>
    suspend fun updateItem(transaction: Transaction): Result<Exception, Unit>
    suspend fun pushItem(
        itemId: String? = null,
        itemsIds: List<String>? = null
    ): Result<Exception, Unit>

    suspend fun deleteItem(
        itemId: String? = null,
        itemsIds: List<String>? = null,
    ): Result<Exception, Unit>

    suspend fun activateItem(
        itemId: String? = null,
        itemsIds: List<String>? = null,
        isActive: Boolean
    ): Result<Exception, Unit>

    suspend fun getItemList(localOnly: Unit? = null): Result<Exception, List<Transaction>>
    suspend fun filterItemList(item: Transaction): Result<Exception, List<Transaction>>

    suspend fun getCalendarListStatics(): Result<Exception, List<TransactionCalendarStatics>>
    suspend fun getTransactionBalanceList(): Result<Exception, List<TransactionBalance>>

    suspend fun clearListCacheTime(): Result<Exception, Unit>
    suspend fun calculateBalance(list: List<Transaction>): Result<Exception, List<TransactionBalance>>

}