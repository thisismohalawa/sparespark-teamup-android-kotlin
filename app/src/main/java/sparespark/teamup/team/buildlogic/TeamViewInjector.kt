package sparespark.teamup.team.buildlogic

import android.app.Application
import sparespark.teamup.home.base.BaseInjector

open class TeamViewInjector(
    app: Application
) : BaseInjector(app) {

    fun provideTeamListViewModelFactory() = TeamViewModelFactory(getTeamRepository())
}
