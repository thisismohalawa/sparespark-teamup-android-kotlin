package sparespark.teamup

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen

class TeamApp : Application() {


    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this@TeamApp)
    }
}
