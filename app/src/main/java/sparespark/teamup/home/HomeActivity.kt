package sparespark.teamup.home

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import sparespark.teamup.R
import sparespark.teamup.auh.AuthActivity
import sparespark.teamup.core.makeToast
import sparespark.teamup.core.restartActivity
import sparespark.teamup.core.setToolbarTitleFont
import sparespark.teamup.core.showSnackBar
import sparespark.teamup.core.visible
import sparespark.teamup.core.wrapper.Result
import sparespark.teamup.data.intentApi.ShareAPIImpl
import sparespark.teamup.data.service.BackupService
import sparespark.teamup.databinding.ActivityHomeBinding
import sparespark.teamup.home.buildlogic.HomeViewInjector

private const val STORAGE_PERMISSION_CODE = 101

class HomeActivity : AppCompatActivity(), HomeViewInteract {
    private lateinit var hBinding: ActivityHomeBinding
    private lateinit var navController: NavController
    private lateinit var viewModel: HomeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupDataBindingUtil()
        setupActionBarSupport()
        setupNavigationController()
        setupViewModel()
        viewModel.startObserving()
    }

    private fun setupDataBindingUtil() {
        hBinding = DataBindingUtil.setContentView(this@HomeActivity, R.layout.activity_home)
    }

    private fun setupActionBarSupport() {
        hBinding.contentHome.toolBar.let {
            setSupportActionBar(it)
            it.setToolbarTitleFont()
        }
    }

    private fun setupNavigationController() = try {
        val navHostFragment: NavHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment
        navController = navHostFragment.findNavController()
        hBinding.contentHome.bottomNav.setupWithNavController(navController)
    } catch (ex: Exception) {
        ex.printStackTrace()
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            owner = this, HomeViewInjector(this.application).provideViewModelFactory()
        )[HomeViewModel::class.java]
        viewModel.handleEvent(HomeViewEvent.OnStartGetUser)
    }

    private fun HomeViewModel.startObserving() {
        loading.observe(this@HomeActivity) {
            updateProgressLoad(loading = it)
        }
        error.observe(this@HomeActivity) {
            displayToast(it.asString(this@HomeActivity))
        }
        actionToolbarTitle.observe(this@HomeActivity, Observer {
            updateActionBarTitle((it.uiResource.asString(this@HomeActivity)))
            updateActionBarTitleColor(it.isError)
        })
        loginAttempt.observe(this@HomeActivity) {
            startAuthActivity()
        }
        requestPermissionsAttempt.observe(this@HomeActivity) {
            checkWriteStoragePermission()
        }
    }

    private fun checkWriteStoragePermission() {
        if (ContextCompat.checkSelfPermission(
                this@HomeActivity, android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_DENIED
        ) ActivityCompat.requestPermissions(
            this@HomeActivity, arrayOf(
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ), STORAGE_PERMISSION_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) makeToast(
                getString(R.string.permission_granted_storage)
            )
            else makeToast(getString(R.string.permission_denied_storage))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.home_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_cache_pref -> {
                navController.navigateUp()
                navController.navigate(R.id.cachePreferenceView)
                true
            }

            R.id.menu_advance_pref -> {
                navController.navigateUp()
                navController.navigate(R.id.advancePreferenceView)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun displayToast(msg: String?) = makeToast(msg ?: getString(R.string.connecting))

    override fun displaySnack(msg: String, duration: Int, aMsg: String?, action: (() -> Unit)?) =
        hBinding.contentHome.fragmentContainer.showSnackBar(msg, duration, aMsg, action)

    override fun restartHomeActivity() = restartActivity()

    override fun finishHomeActivity() = finish()

    override fun startAuthActivity() = startActivity(
        Intent(this@HomeActivity, AuthActivity::class.java)
    ).also { this@HomeActivity.finish() }

    override fun actionDial(phoneNum: String) {
        ShareAPIImpl(this@HomeActivity).dial(phoneNum)
    }

    override fun actionMsgWhatsApp(phoneNum: String) {
        ShareAPIImpl(this@HomeActivity).messageVieWhatsApp(phoneNum)
    }

    override fun actionCopyText(text: String) {
        val result = ShareAPIImpl(this@HomeActivity).copyText(text)
        if (result is Result.Value) displayToast(getString(R.string.copied))
    }

    override fun actionShareText(text: String) {
        ShareAPIImpl(this@HomeActivity).shareText(text)
    }

    override fun actionDataExport(intentAction: String) {
        val serviceIntent = Intent(this@HomeActivity, BackupService::class.java)
        serviceIntent.action = intentAction
        this@HomeActivity.startService(serviceIntent)
    }

    override fun updateProgressLoad(loading: Boolean) {
        hBinding.contentHome.progressCircular.visible(loading)
    }

    override fun updateActionBarTitle(title: String?) {
        supportActionBar?.subtitle = title ?: getString(R.string.connecting)
    }

    override fun updateActionBarTitleColor(isError: Boolean) {
        if (isError) hBinding.contentHome.toolBar.setSubtitleTextColor(
            ContextCompat.getColor(
                this@HomeActivity, R.color.red
            )
        ) else Unit
    }
}
