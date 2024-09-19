package sparespark.teamup.data.preference.selector

import android.content.Context
import sparespark.teamup.core.launchASuspendTaskScope
import sparespark.teamup.core.wrapper.Result
import sparespark.teamup.data.preference.BasePreferenceProvider

private const val SELECT_KEY = "SELECT_KEY"

class LocalSelectorRepositoryImpl(
    context: Context
) : BasePreferenceProvider(context), LocalSelectorRepository {
    private fun Set<String>?.toMutableSetX() = this?.toMutableSet() ?: mutableSetOf()

    override suspend fun getSelectedSet(): Result<Exception, MutableSet<String>> = Result.build {
        launchASuspendTaskScope {
            val hashSet = sharedPref.getStringSet(SELECT_KEY, mutableSetOf())
            return@launchASuspendTaskScope hashSet.toMutableSetX()
        }
    }

    override suspend fun clearSelectedSet(): Result<Exception, Unit> = Result.build {
        launchASuspendTaskScope {
            prefEditor.clear().commit()
        }
    }

    override suspend fun addSelector(id: String): Result<Exception, Unit> = Result.build {
        launchASuspendTaskScope {
            val hashSet = sharedPref.getStringSet(SELECT_KEY, mutableSetOf())
            val mutableSet = hashSet.toMutableSetX()
            mutableSet.add(id)
            prefEditor.remove(SELECT_KEY).also {
                it.putStringSet(SELECT_KEY, mutableSet)
                it.commit()
            }
        }
    }

    override suspend fun removeSelector(id: String): Result<Exception, Unit> = Result.build {
        launchASuspendTaskScope {
            val hashSet = sharedPref.getStringSet(SELECT_KEY, mutableSetOf())
            val mutableSet = hashSet.toMutableSetX()
            mutableSet.remove(id)
            prefEditor.remove(SELECT_KEY).also {
                it.putStringSet(SELECT_KEY, mutableSet)
                it.commit()
            }
        }
    }

    override suspend fun isSelectedSet(id: String): Result<Exception, Boolean> = Result.build {
        launchASuspendTaskScope {
            val hashSet = sharedPref.getStringSet(SELECT_KEY, mutableSetOf())
            hashSet.toMutableSetX().contains(id)
        }
    }
}