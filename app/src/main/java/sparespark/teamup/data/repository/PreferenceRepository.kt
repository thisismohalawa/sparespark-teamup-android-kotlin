package sparespark.teamup.data.repository

import sparespark.teamup.core.wrapper.Result

interface PreferenceRepository {
    fun getUseCalenderStatus(): Boolean
    fun getUseListStaticsStatus(): Boolean
    fun getUseNoteStatus(): Boolean
    fun getSuggestShareStatus(): Boolean
    fun getSuggestAddStatus(): Boolean
    suspend fun updatePrefRemoteServerUse(enable: Boolean): Result<Exception, Unit>
    suspend fun updatePrefAutoBackupUse(enable: Boolean): Result<Exception, Unit>
    suspend fun resetPrefInputCacheTimes(): Result<Exception, Unit>
    suspend fun clearPrefLastCacheTimes(): Result<Exception, Unit>
    suspend fun clearDatabase(): Result<Exception, Unit>
}
