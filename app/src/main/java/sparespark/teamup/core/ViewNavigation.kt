package sparespark.teamup.core

import android.content.Intent
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import sparespark.teamup.home.HomeActivity

internal fun Fragment.relaunchCurrentView() {
    view?.post {
        findNavController().apply {
            val id = currentDestination?.id
            id?.let {
                popBackStack(it, true)
                navigate(it)
            }
        }
    }
}

internal fun HomeActivity.restartActivity() {
    val intent: Intent? = applicationContext.packageManager
        .getLaunchIntentForPackage(applicationContext.packageName)
    intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
    startActivity(intent)
}