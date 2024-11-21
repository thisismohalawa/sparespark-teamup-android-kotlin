package sparespark.teamup.notedetails.buildlogic

import android.app.Application
import sparespark.teamup.core.base.BaseInjector

class NoteInjector(
    app: Application
) : BaseInjector(app) {
    fun provideViewModelFactory() = NoteViewModelFactory(getNoteRepository())
}
