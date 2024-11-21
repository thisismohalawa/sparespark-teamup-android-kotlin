package sparespark.teamup.auth

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import sparespark.teamup.R
import sparespark.teamup.core.makeToast
import sparespark.teamup.home.HomeActivity

class AuthActivity : AppCompatActivity(), AuthActivityInteract {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
    }

    override fun displayToast(msg: String) = makeToast(msg)

    override fun startDataActivity() = startActivity(
        Intent(this@AuthActivity, HomeActivity::class.java)
    ).also { this@AuthActivity.finish() }
}