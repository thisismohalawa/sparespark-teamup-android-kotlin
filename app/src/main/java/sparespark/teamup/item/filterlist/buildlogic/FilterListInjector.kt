package sparespark.teamup.item.filterlist.buildlogic

import android.app.Application
import sparespark.teamup.item.BaseItemInjector

class FilterListInjector(
    app: Application
) : BaseItemInjector(app) {
    fun provideViewModelFactory() = FilterListViewModelFactory(
        getItemRepository(),
        getStaticsRepository(),
        getSelectorRepository(),
        getPreferenceRepository()
    )
}
