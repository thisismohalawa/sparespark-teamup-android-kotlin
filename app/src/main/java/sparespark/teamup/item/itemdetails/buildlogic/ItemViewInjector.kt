package sparespark.teamup.item.itemdetails.buildlogic

import android.app.Application
import sparespark.teamup.item.BaseItemInjector

class ItemViewInjector(
    app: Application
) : BaseItemInjector(app) {

    fun provideViewModelFactory() =
        ItemViewModelFactory(getItemRepository(), getClientRepository(), getPreferenceRepository())
}
