package sparespark.teamup.home

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import sparespark.teamup.R
import sparespark.teamup.auth.AuthActivity
import sparespark.teamup.core.STORAGE_PERMISSION_CODE
import sparespark.teamup.core.makeToast
import sparespark.teamup.core.visible
import sparespark.teamup.core.wrapper.Result
import sparespark.teamup.data.shareApi.ShareAPIImpl
import sparespark.teamup.databinding.ActivityHomeBinding
import sparespark.teamup.home.buildlogic.HomeActivityInjector


class HomeActivity : HomeActivityUgly(), HomeActivityInteract {
    private lateinit var hBinding: ActivityHomeBinding
    private lateinit var navController: NavController
    private lateinit var viewModel: HomeActivityViewModel

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
            owner = this, HomeActivityInjector(this.application).provideViewModelFactory()
        )[HomeActivityViewModel::class.java]
        viewModel.handleEvent(HomeActivityEvent.OnStartGetUser)
    }

    private fun HomeActivityViewModel.startObserving() {
        loading.observe(this@HomeActivity) {
            updateProgressLoad(loading = it)
        }
        error.observe(this@HomeActivity) {
            displayToast(it.asString(this@HomeActivity))
        }
        actionToolbarTitle.observe(this@HomeActivity) {
            updateActionBarTitle((it.uiResource.asString(this@HomeActivity)))
            updateActionBarTitleColor(it.isError)
        }
        loginAttempt.observe(this@HomeActivity) {
            startAuthActivity()
        }
        requestPermissionsAttempt.observe(this@HomeActivity) {
            checkWriteStoragePermission()
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

            R.id.menu_datasource_pref -> {
                navController.navigateUp()
                navController.navigate(R.id.datasourcePreference)
                true
            }

            R.id.menu_advance_pref -> {
                navController.navigateUp()
                navController.navigate(R.id.advancedPreferenceView)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun displayToast(msg: String?) = makeToast(msg ?: getString(R.string.connecting))

    override fun displaySnack(msg: String, duration: Int, aMsg: String?, action: (() -> Unit)?) =
        hBinding.contentHome.fragmentContainer.showSnackBar(msg, duration, aMsg, action)

    override fun startAuthActivity() = startActivity(
        Intent(this@HomeActivity, AuthActivity::class.java)
    ).also { this@HomeActivity.finish() }

    override fun restartHomeActivity() {
        val intent: Intent? = applicationContext.packageManager
            .getLaunchIntentForPackage(applicationContext.packageName)
        intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    override fun finishHomeActivity() = finish()

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

    override fun actionCopyText(text: String) {
        val result = ShareAPIImpl(this@HomeActivity).copyText(text)
        if (result is Result.Value) displayToast(getString(R.string.copied))
    }

    override fun actionShareText(text: String) {
        ShareAPIImpl(this@HomeActivity).shareText(text)
    }

    override fun actionDial(phoneNum: String) {
        ShareAPIImpl(this@HomeActivity).dial(phoneNum)
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

}
