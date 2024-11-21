package sparespark.teamup.data.repository

import sparespark.teamup.core.wrapper.Result

interface PreferenceRepository : AdvancedPreferenceRepository {
    suspend fun updateRemoteServerUse(enable: Boolean): Result<Exception, Unit>
    suspend fun updateAutoBackupUse(enable: Boolean): Result<Exception, Unit>
    suspend fun resetInputCacheTimes(): Result<Exception, Unit>
    suspend fun clearLastCacheTimes(): Result<Exception, Unit>
    suspend fun clearAppDatabase(): Result<Exception, Unit>
}
