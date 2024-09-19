package sparespark.teamup.data.repository

import sparespark.teamup.core.wrapper.Result
import sparespark.teamup.data.model.city.City

interface CityRepository {
    suspend fun getCityList(localOnly: Unit? = null): Result<Exception, List<City>>
    suspend fun updateCity(city: City): Result<Exception, Unit>
    suspend fun deleteCity(id: String): Result<Exception, Unit>
    suspend fun clearListCacheTime(): Result<Exception, Unit>
}
