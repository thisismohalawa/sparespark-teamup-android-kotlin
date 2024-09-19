package sparespark.teamup.auh

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import sparespark.teamup.R
import sparespark.teamup.core.makeToast
import sparespark.teamup.home.HomeActivity

class AuthActivity : AppCompatActivity(), AuthViewInteract {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
    }

    override fun displayToast(msg: String) = makeToast(msg)

    override fun startDataActivity() = startActivity(
        Intent(this@AuthActivity, HomeActivity::class.java)
    ).also { this@AuthActivity.finish() }
}