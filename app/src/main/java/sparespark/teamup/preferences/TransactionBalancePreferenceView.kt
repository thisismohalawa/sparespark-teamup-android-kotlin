package sparespark.teamup.preferences

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import sparespark.teamup.R

class TransactionBalancePreferenceView : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.transaction_balance_preference)
    }
}