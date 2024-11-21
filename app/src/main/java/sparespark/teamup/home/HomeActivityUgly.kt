package sparespark.teamup.home

import android.content.pm.PackageManager
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.snackbar.Snackbar
import sparespark.teamup.R
import sparespark.teamup.core.STORAGE_PERMISSION_CODE


open class HomeActivityUgly : AppCompatActivity() {
    protected fun androidx.appcompat.widget.Toolbar.setToolbarTitleFont() = try {
        for (i in 0 until childCount) {
            val view = getChildAt(i)
            if (view is TextView && view.text == title) {
                view.typeface = ResourcesCompat.getFont(context, R.font.tango_bold)
                break
            }
        }
    } catch (ex: Exception) {
        ex.printStackTrace()
    }

    protected fun checkWriteStoragePermission() {
        if (ContextCompat.checkSelfPermission(
                this@HomeActivityUgly, android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_DENIED
        ) ActivityCompat.requestPermissions(
            this@HomeActivityUgly, arrayOf(
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ), STORAGE_PERMISSION_CODE
        )
    }

    protected fun View?.showSnackBar(
        msg: String,
        duration: Int,
        aMsg: String?,
        action: (() -> Unit)?
    ) {
        this@showSnackBar?.let {
            Snackbar.make(it, msg, duration).apply {
                if (aMsg != null) setAction(aMsg) {
                    action?.let { it() }
                }
                show()
            }
        }

    }
}