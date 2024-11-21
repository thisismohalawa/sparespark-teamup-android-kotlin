package sparespark.teamup.data.repository

import sparespark.teamup.core.wrapper.Result
import sparespark.teamup.data.model.statics.StockStatics
import sparespark.teamup.data.model.stock.Stock

interface StockRepository {
    suspend fun getItemListHintTitle(): Result<Exception, List<Int>>

    suspend fun getItemById(itemId: String): Result<Exception, Stock>
    suspend fun updateItem(item: Stock): Result<Exception, Unit>
    suspend fun pushItem(
        itemId: String? = null,
        itemsIds: List<String>? = null
    ): Result<Exception, Unit>
    suspend fun deleteItem(
        itemId: String? = null,
        itemsIds: List<String>? = null,
    ): Result<Exception, Unit>

    suspend fun getItemList(localOnly: Unit? = null): Result<Exception, List<Stock>>
    suspend fun filterItemList(stock: Stock): Result<Exception, List<Stock>>

    suspend fun getAvailableAssetQuantity(stock: Stock): Result<Exception, Double>

    suspend fun clearListCacheTime(): Result<Exception, Unit>
    suspend fun calculateListStatics(list: List<Stock>): Result<Exception, List<StockStatics>>

}