package sparespark.teamup.data.repository

import sparespark.teamup.core.wrapper.Result
import sparespark.teamup.data.model.item.Item
import sparespark.teamup.data.model.statics.CStatics
import sparespark.teamup.data.model.statics.LStatics


interface StaticsRepository {
    suspend fun getCalenderStatics(): Result<Exception, CStatics>
    suspend fun getListStatics(): Result<Exception, LStatics>
    suspend fun calculateListStatics(list: List<Item>): Result<Exception, LStatics>
    suspend fun calculateCalenderStatics(list: List<Item>): Result<Exception, CStatics>
    suspend fun clearListCacheTime(): Result<Exception, Unit>
}
