package sparespark.teamup.data.preference.selector

import sparespark.teamup.core.wrapper.Result

interface LocalSelectorRepository {
    suspend fun getSelectedSet(): Result<Exception, MutableSet<String>>
    suspend fun clearSelectedSet(): Result<Exception, Unit>
    suspend fun addSelector(id: String): Result<Exception, Unit>
    suspend fun removeSelector(id: String): Result<Exception, Unit>
    suspend fun isSelectedSet(id: String): Result<Exception, Boolean>
}