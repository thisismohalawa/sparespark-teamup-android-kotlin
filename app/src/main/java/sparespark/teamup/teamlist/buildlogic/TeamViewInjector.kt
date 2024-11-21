package sparespark.teamup.teamlist.buildlogic

import android.app.Application
import sparespark.teamup.core.base.BaseInjector

open class TeamViewInjector(
    app: Application
) : BaseInjector(app) {

    fun provideTeamListViewModelFactory() = TeamViewModelFactory(getTeamRepository())
}
