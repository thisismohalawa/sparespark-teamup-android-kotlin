package sparespark.teamup.data.repository

import sparespark.teamup.core.wrapper.Result
import sparespark.teamup.data.model.item.Item

interface ItemRepository {
    suspend fun updateItem(item: Item): Result<Exception, Unit>
    suspend fun getItemById(itemId: String): Result<Exception, Item>
    suspend fun getItemList(localOnly: Unit? = null): Result<Exception, List<Item>>
    suspend fun clearListCacheTime(): Result<Exception, Unit>
    suspend fun getItemListHintTitle(): Result<Exception, List<Int>>
    suspend fun getFilteredItemListHintTitle(): Result<Exception, List<Int>>
    suspend fun activateItem(
        itemId: String? = null,
        itemsIds: List<String>? = null,
        isActive: Boolean
    ): Result<Exception, Unit>

    suspend fun deleteItem(
        itemId: String? = null,
        itemsIds: List<String>? = null,
    ): Result<Exception, Unit>

    suspend fun getRemoteItemListByQuery(
        query: String
    ): Result<Exception, List<Item>>

    suspend fun getActiveItemList(): Result<Exception, List<Item>>

    suspend fun getBuyItemList(): Result<Exception, List<Item>>

    suspend fun getAdminItemList(): Result<Exception, List<Item>>

    suspend fun filterToActiveList(
        list: List<Item>
    ): Result<Exception, List<Item>>

    suspend fun filterToBuyList(
        list: List<Item>
    ): Result<Exception, List<Item>>

    suspend fun filterToAdminList(
        list: List<Item>
    ): Result<Exception, List<Item>>
}