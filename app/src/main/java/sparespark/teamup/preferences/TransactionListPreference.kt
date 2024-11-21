package sparespark.teamup.preferences

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import sparespark.teamup.R
import sparespark.teamup.core.USE_BACKUP
import sparespark.teamup.home.HomeActivityInteract
import sparespark.teamup.preferences.buildlogic.PreferenceViewInjector
import sparespark.teamup.preferences.buildlogic.PreferenceViewModel

class TransactionListPreference : PreferenceFragmentCompat() {
    private lateinit var viewInteract: HomeActivityInteract
    private lateinit var viewModel: PreferenceViewModel

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.transaction_list_preference)
        setupInteract()
        setupViews()
        setupViewModel()
        viewModel.viewModelStateObserver()
    }

    private fun setupInteract() {
        viewInteract = activity as HomeActivityInteract
    }

    private fun setupViews() {
        val swBackup: SwitchPreference? = findPreference(USE_BACKUP)

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

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            owner = this@TransactionListPreference,
            factory = PreferenceViewInjector(requireActivity().application).provideViewModelFactory()
        )[PreferenceViewModel::class.java]
    }

    private fun PreferenceViewModel.viewModelStateObserver() {
        error.observe(this@TransactionListPreference) {
            viewInteract.displayToast(it.asString(requireContext()))
        }
        backupText.observe(this@TransactionListPreference) {
            viewInteract.displayToast(it.asString(context))
        }
    }
}