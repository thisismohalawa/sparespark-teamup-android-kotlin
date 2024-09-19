package sparespark.teamup

import android.app.Application
import com.google.firebase.FirebaseApp
import com.jakewharton.threetenabp.AndroidThreeTen

class TeamApp : Application() {

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this@TeamApp)
        AndroidThreeTen.init(this@TeamApp)
    }
}
