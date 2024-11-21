package sparespark.teamup.auth.login

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import sparespark.teamup.R
import sparespark.teamup.auth.AuthActivityInteract
import sparespark.teamup.auth.AuthEvent
import sparespark.teamup.auth.BaseAuthView
import sparespark.teamup.auth.login.buildlogic.LoginInjector
import sparespark.teamup.core.binding.ViewBindingHolder
import sparespark.teamup.core.binding.ViewBindingHolderImpl
import sparespark.teamup.core.enable
import sparespark.teamup.core.internal.SIGN_IN_REQUEST_CODE
import sparespark.teamup.core.visible
import sparespark.teamup.data.model.LoginResult
import sparespark.teamup.databinding.AuthViewBinding

class LoginView : BaseAuthView(), View.OnClickListener,
    ViewBindingHolder<AuthViewBinding> by ViewBindingHolderImpl() {

    private lateinit var viewInteract: AuthActivityInteract
    private lateinit var viewModel: LoginViewModel

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.btn_signup -> if (findNavController().currentDestination?.id == R.id.loginView)
                findNavController().navigate(LoginViewDirections.navigateToSignup()) else Unit

            R.id.btn_google_login -> viewModel.handleEvent(AuthEvent.OnAuthBtnClick)
            R.id.btn_auth -> viewModel.handleEvent(
                AuthEvent.OnLoginBtnClick(
                    email = binding?.itemEmail?.edText?.text?.trim().toString(),
                    pass = binding?.itemPassword?.edText?.text?.trim().toString()
                )
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        destroyBinding()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = initBinding(AuthViewBinding.inflate(layoutInflater), this@LoginView) {
        setupViewInteract()
        setupViews()
        setupViewModel()
        viewModel.startObserving()
    }

    private fun setupViewInteract() {
        viewInteract = activity as AuthActivityInteract
    }

    private fun setupViews() {
        binding?.apply {
            btnAuth.setOnClickListener(this@LoginView)
            btnSignup.setOnClickListener(this@LoginView)
            btnGoogleLogin.setOnClickListener(this@LoginView)
            itemEmail.edText.setUserEmailInput(
                inputLayout = itemEmail.textInputLayout
            )
            itemPassword.edText.setUserPasswordInput(
                inputLayout = itemPassword.textInputLayout
            )
            (btnGoogleLogin.getChildAt(0) as TextView).text =
                getString(R.string.continue_with_google)
        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            owner = this@LoginView,
            factory = LoginInjector(requireActivity().application).provideViewModelFactory()
        )[LoginViewModel::class.java]
        viewModel.handleEvent(AuthEvent.GetAuthUser)
    }

    private fun startSignInFlow() = try {
        val gso: GoogleSignInOptions =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build()

        val googleSignInClient: GoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

        val signInIntent: Intent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, SIGN_IN_REQUEST_CODE)
    } catch (ex: Exception) {
        viewInteract.displayToast(getString(R.string.unable_to_sign_in))
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)/*
        *
        *  +TO DO
        * Enable Google Sign in method
        *
        * Add a support email address to your project in project settings.
        * Open link https://console.firebase.google.com/
        *
        * */
        try {
            val userToken: String?
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)

            val account: GoogleSignInAccount? = task.getResult(ApiException::class.java)
            if (account != null) {
                userToken = account.idToken
                viewModel.handleEvent(
                    AuthEvent.OnGoogleSignInResult(
                        LoginResult(requestCode, userToken)
                    )
                )
            }
        } catch (ex: Exception) {
            viewInteract.displayToast(
                getString(R.string.unable_to_sign_in) +
                        ex.message
            )
        }
    }

    private fun LoginViewModel.startObserving() {
        loading.observe(viewLifecycleOwner) {
            binding?.progressBar?.visible(it)
        }
        error.observe(viewLifecycleOwner) {
            viewInteract.displayToast(it.asString(context) ?: getString(R.string.connecting))
        }
        emailTxtValidateState.observe(viewLifecycleOwner) {
            binding?.itemEmail?.textInputLayout?.error = getString(R.string.invalid)
        }
        passwordTxtValidateState.observe(viewLifecycleOwner) {
            binding?.itemPassword?.textInputLayout?.error = getString(R.string.invalid)
        }
        loginButtonText.observe(viewLifecycleOwner) {
            binding?.btnAuth?.text = it.asString(context)
        }
        signable.observe(viewLifecycleOwner) {
            binding?.apply {
                btnAuth.enable(it)
                btnSignup.visible(it)
                txtOr.visible(it)
                btnGoogleLogin.visible(it)
            }
        }
        googleAuthAttempt.observe(viewLifecycleOwner) {
            startSignInFlow()
        }
        updated.observe(viewLifecycleOwner) {
            viewInteract.startDataActivity()
        }
    }
}