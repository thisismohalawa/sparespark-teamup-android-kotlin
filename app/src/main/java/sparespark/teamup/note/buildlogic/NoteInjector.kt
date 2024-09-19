package sparespark.teamup.note.buildlogic

import android.app.Application
import sparespark.teamup.home.base.BaseInjector

class NoteInjector(
    app: Application
) : BaseInjector(app) {

    fun provideViewModelFactory() =
        NoteViewModelFactory(getNoteRepository(), getPreferenceRepository())
}
