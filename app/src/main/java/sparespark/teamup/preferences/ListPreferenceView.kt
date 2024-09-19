package sparespark.teamup.preferences

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import sparespark.teamup.R
import sparespark.teamup.core.USE_BACKUP
import sparespark.teamup.core.USE_REMOTE_SERVER
import sparespark.teamup.home.HomeViewInteract
import sparespark.teamup.preferences.base.PreferenceEvent
import sparespark.teamup.preferences.buildlogic.PreferenceViewInjector
import sparespark.teamup.preferences.base.PreferenceViewModel

class ListPreferenceView : PreferenceFragmentCompat() {
    private lateinit var viewInteract: HomeViewInteract
    private lateinit var viewModel: PreferenceViewModel

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.list_preferences)
        setupInteract()
        setupViewModel()
        setupViews()
        viewModel.startObserving()
    }

    private fun setupInteract() {
        viewInteract = activity as HomeViewInteract
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            owner = this@ListPreferenceView,
            factory = PreferenceViewInjector(requireActivity().application).provideViewModelFactory()
        )[PreferenceViewModel::class.java]
    }

    private fun setupViews() {
        val swRemoteServer: SwitchPreference? = findPreference(USE_REMOTE_SERVER)
        val swBackup: SwitchPreference? = findPreference(USE_BACKUP)

        swRemoteServer?.onPreferenceChangeListener =
            Preference.OnPreferenceChangeListener { _, newValue ->
                activity?.let {
                    if (newValue == true) {
                        swRemoteServer!!.isChecked = true
                        viewModel.handleEvent(
                            PreferenceEvent.OnServerSwitchUpdate(
                                enable = true
                            )
                        )
                    } else {
                        swRemoteServer!!.isChecked = false
                        viewModel.handleEvent(
                            PreferenceEvent.OnServerSwitchUpdate(
                                enable = false
                            )
                        )
                    }
                }
                false
            }
        swBackup?.onPreferenceChangeListener =
            Preference.OnPreferenceChangeListener { _, newValue ->
                activity?.let {
                    if (newValue == true) {
                        swBackup!!.isChecked = true
                        viewModel.handleEvent(
                            PreferenceEvent.OnBackupSwitchUpdate(
                                enable = true
                            )
                        )
                    } else {
                        swBackup!!.isChecked = false
                        viewModel.handleEvent(
                            PreferenceEvent.OnBackupSwitchUpdate(
                                enable = false
                            )
                        )
                    }
                }
                false
            }
    }

    private fun PreferenceViewModel.startObserving() {
        error.observe(this@ListPreferenceView) {
            viewInteract.displayToast(it.asString(context))
        }
        backup.observe(this@ListPreferenceView){
            viewInteract.displayToast(it.asString(context))
        }
    }
}
