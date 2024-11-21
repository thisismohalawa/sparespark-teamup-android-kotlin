package sparespark.teamup.data.repository

import sparespark.teamup.core.wrapper.Result
import sparespark.teamup.data.model.client.Client

interface ClientRepository {
    suspend fun getClientList(localOnly: Unit? = null): Result<Exception, List<Client>>
    suspend fun updateClient(client: Client): Result<Exception, Unit>
    suspend fun deleteClient(
        itemId: String? = null,
        itemsIds: List<String>? = null,
    ): Result<Exception, Unit>

    suspend fun getClientCityByName(name: String): Result<Exception, String>
    suspend fun clearListCacheTime(): Result<Exception, Unit>
}
