package sparespark.teamup.data.repository

interface AdvancedPreferenceRepository {
    fun getSuggestAddStatus(): Boolean
    fun getSuggestShareStatus(): Boolean
    fun getUseNoteStatus(): Boolean
    fun getUseStaticsStatus(): Boolean
    fun getUseBalanceStatus(): Boolean

}