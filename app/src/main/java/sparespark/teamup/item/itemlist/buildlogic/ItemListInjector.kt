package sparespark.teamup.item.itemlist.buildlogic

import android.app.Application
import sparespark.teamup.item.BaseItemInjector

class ItemListInjector(
    app: Application
) : BaseItemInjector(app) {
    fun provideViewModelFactory() = ItemListViewModelFactory(
        getItemRepository(),
        getStaticsRepository(),
        getSelectorRepository(),
        getNoteRepository(),
        getPreferenceRepository()
    )
}
