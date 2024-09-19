package sparespark.teamup.preferences

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import sparespark.teamup.R

class AdvancePreferenceView : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.advance_preferences)
    }
}
