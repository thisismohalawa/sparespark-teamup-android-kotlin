package sparespark.teamup.preferences

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.preference.EditTextPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import sparespark.teamup.R
import sparespark.teamup.core.CITY_CACHE_TIME
import sparespark.teamup.core.CLIENT_CACHE_TIME
import sparespark.teamup.core.EXPENSE_CACHE_TIME
import sparespark.teamup.core.ITEM_CACHE_TIME
import sparespark.teamup.core.NOTE_CACHE_TIME
import sparespark.teamup.core.setNumberDecimalPrefInput
import sparespark.teamup.home.HomeViewInteract
import sparespark.teamup.preferences.base.PreferenceEvent
import sparespark.teamup.preferences.buildlogic.PreferenceViewInjector
import sparespark.teamup.preferences.base.PreferenceViewModel

private const val CLEAR_CACHE = "CLEAR CACHE"
private const val RESET_CACHE = "RESET CACHE"

class CachePreferenceView : PreferenceFragmentCompat() {
    private lateinit var viewInteract: HomeViewInteract
    private lateinit var viewModel: PreferenceViewModel

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.cache_preference)
        setupInteract()
        setupViewModel()
        setupViews()
        viewModel.viewModelStateObserver()
    }

    private fun setupInteract() {
        viewInteract = activity as HomeViewInteract
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            owner = this@CachePreferenceView,
            factory = PreferenceViewInjector(requireActivity().application).provideViewModelFactory()
        )[PreferenceViewModel::class.java]
    }

    private fun setupViews() {
        val edCity: EditTextPreference? = findPreference(CITY_CACHE_TIME)
        val edClient: EditTextPreference? = findPreference(CLIENT_CACHE_TIME)
        val edExpense: EditTextPreference? = findPreference(EXPENSE_CACHE_TIME)
        val edNote: EditTextPreference? = findPreference(NOTE_CACHE_TIME)
        val edItem: EditTextPreference? = findPreference(ITEM_CACHE_TIME)

        val btnResetCache: Preference? = findPreference(RESET_CACHE)
        val btnClearCache: Preference? = findPreference(CLEAR_CACHE)

        btnResetCache?.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            viewModel.handleEvent(PreferenceEvent.OnResetCacheClick)
            true
        }
        btnClearCache?.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            viewModel.handleEvent(PreferenceEvent.OnClearCacheClick)
            true
        }
        edCity?.setNumberDecimalPrefInput()
        edClient?.setNumberDecimalPrefInput()
        edExpense?.setNumberDecimalPrefInput()
        edNote?.setNumberDecimalPrefInput()
        edItem?.setNumberDecimalPrefInput()
    }

    private fun PreferenceViewModel.viewModelStateObserver() {
        error.observe(this@CachePreferenceView) {
            viewInteract.displayToast(it.asString(requireContext()))
        }
        cacheUpdated.observe(this@CachePreferenceView) {
            viewInteract.displayToast(getString(R.string.updated_success))
        }
        dbCleared.observe(this@CachePreferenceView) {
            viewInteract.restartHomeActivity()
        }
    }
}