package sparespark.teamup.data.preference.advance

interface AdvancedPreference {
    fun isNotesUsed(): Boolean
    fun isShareActionUsed(): Boolean
    fun isAddNewActionUsed(): Boolean
    fun getUseStaticsStatus(): Boolean
    fun getUseBalanceStatus(): Boolean
}
