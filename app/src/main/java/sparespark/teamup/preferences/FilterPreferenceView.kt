package sparespark.teamup.preferences

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import sparespark.teamup.R

class FilterPreferenceView : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.filter_preferences)
    }
}
