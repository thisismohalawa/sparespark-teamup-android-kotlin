package sparespark.teamup.preferences

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import sparespark.teamup.R

class AdvancedPreferenceView : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.advanced_preferences)
    }
}
