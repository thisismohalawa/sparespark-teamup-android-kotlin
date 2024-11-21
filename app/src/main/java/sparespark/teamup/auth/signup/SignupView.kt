package sparespark.teamup.auth.signup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import sparespark.teamup.auth.BaseAuthView
import sparespark.teamup.R
import sparespark.teamup.auth.AuthActivityInteract
import sparespark.teamup.auth.AuthEvent
import sparespark.teamup.auth.signup.buildlogic.SignUpInjector
import sparespark.teamup.core.binding.ViewBindingHolderImpl
import sparespark.teamup.core.visible
import sparespark.teamup.databinding.AuthViewBinding
import sparespark.teamup.core.binding.ViewBindingHolder

class SignupView : BaseAuthView(), View.OnClickListener,
    ViewBindingHolder<AuthViewBinding> by ViewBindingHolderImpl() {

    private lateinit var viewInteract: AuthActivityInteract
    private lateinit var viewModel: SignUpViewModel

    override fun onClick(p0: View?) {
        if (p0?.id == R.id.btn_auth) viewModel.handleEvent(
            AuthEvent.OnSignupBtnClick(
                email = binding?.itemEmail?.edText?.text?.trim().toString(),
                pass = binding?.itemPassword?.edText?.text?.trim().toString()
            )
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        destroyBinding()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = initBinding(AuthViewBinding.inflate(layoutInflater), this@SignupView) {
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
            btnSignup.visible(isVisible = false)
            btnGoogleLogin.visible(isVisible = false)
            txtOr.visible(isVisible = false)
            btnAuth.text = getString(R.string.signup)
            btnAuth.setOnClickListener(this@SignupView)
            itemEmail.edText.setUserEmailInput(
                inputLayout = itemEmail.textInputLayout
            )
            itemPassword.edText.setUserPasswordInput(
                inputLayout = itemPassword.textInputLayout
            )
        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            owner = this@SignupView,
            factory = SignUpInjector(requireActivity().application).provideViewModelFactory()
        )[SignUpViewModel::class.java]
    }

    private fun SignUpViewModel.startObserving() {
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
        updated.observe(viewLifecycleOwner) {
            findNavController().navigate(SignupViewDirections.navigateToLogin())
        }
    }
}