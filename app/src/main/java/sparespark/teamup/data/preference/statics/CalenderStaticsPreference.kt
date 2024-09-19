package sparespark.teamup.data.preference.statics

import sparespark.teamup.data.preference.BaseListPreference
import sparespark.teamup.data.model.statics.CStatics

interface CalenderStaticsPreference : BaseListPreference {
    fun getCalenderStatics(): CStatics
    fun updateCalenderStatics(statics: CStatics)
}