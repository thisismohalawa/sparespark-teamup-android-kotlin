package sparespark.teamup.preferences

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import sparespark.teamup.R
import sparespark.teamup.core.USE_BACKUP
import sparespark.teamup.core.USE_REMOTE_SERVER
import sparespark.teamup.home.HomeActivityInteract
import sparespark.teamup.preferences.buildlogic.PreferenceViewInjector
import sparespark.teamup.preferences.buildlogic.PreferenceViewModel

class DatasourcePreference : PreferenceFragmentCompat() {
    private lateinit var viewInteract: HomeActivityInteract
    private lateinit var viewModel: PreferenceViewModel

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.datasource_preference)
        setupInteract()
        setupViews()
        setupViewModel()
        viewModel.viewModelStateObserver()
    }

    private fun setupInteract() {
        viewInteract = activity as HomeActivityInteract
    }

    private fun setupViews() {
        val swRemoteServer: SwitchPreference? = findPreference(USE_REMOTE_SERVER)

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
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            owner = this@DatasourcePreference,
            factory = PreferenceViewInjector(requireActivity().application).provideViewModelFactory()
        )[PreferenceViewModel::class.java]
    }

    private fun PreferenceViewModel.viewModelStateObserver() {
        error.observe(this@DatasourcePreference) {
            viewInteract.displayToast(it.asString(requireContext()))
        }
    }
}