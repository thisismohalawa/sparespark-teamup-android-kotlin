package sparespark.teamup.userprofile.profiledetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import sparespark.teamup.R
import sparespark.teamup.core.MAX_INPUT_NAME_DIG
import sparespark.teamup.core.MIN_INPUT_NAME_DIG
import sparespark.teamup.core.beginLayoutTitleLengthWatcher
import sparespark.teamup.core.binding.ViewBindingHolder
import sparespark.teamup.core.binding.ViewBindingHolderImpl
import sparespark.teamup.core.setPhoneNumInput
import sparespark.teamup.core.toEditable
import sparespark.teamup.databinding.ProfiledetailsViewBinding
import sparespark.teamup.home.HomeActivityInteract
import sparespark.teamup.userprofile.UserEvent
import sparespark.teamup.userprofile.profiledetails.buildlogic.ProfileDetailViewInjector

class ProfileDetailView : Fragment(), View.OnClickListener,
    ViewBindingHolder<ProfiledetailsViewBinding> by ViewBindingHolderImpl() {

    private lateinit var viewModel: ProfileDetailViewModel
    private lateinit var viewInteract: HomeActivityInteract

    override fun onDestroyView() {
        super.onDestroyView()
        destroyBinding()
    }

    override fun onClick(p0: View?) {
        if (p0?.id == R.id.btn_action) viewModel.handleEvent(
            UserEvent.OnUpdateBtnClick(
                name = binding?.itemName?.edText?.text?.trim().toString(),
                phone = binding?.itemPhone?.edText?.text?.trim().toString()
            )
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View =
        initBinding(ProfiledetailsViewBinding.inflate(layoutInflater), this@ProfileDetailView) {
            setupViewInteract()
            setupViewInputs()
            setupViewModel()
            viewModel.startObserving()
            btnAction.setOnClickListener(this@ProfileDetailView)

        }

    private fun setupViewInteract() {
        viewInteract = activity as HomeActivityInteract
    }

    private fun setupViewInputs() {
        binding?.apply {
            itemName.textInputLayout.hint = getString(R.string.user_name)
            itemName.edText.beginLayoutTitleLengthWatcher(
                inputLayout = itemName.textInputLayout,
                minDig = MIN_INPUT_NAME_DIG,
                maxDig = MAX_INPUT_NAME_DIG,
                eMsg = getString(R.string.invalid)
            )
            itemPhone.textInputLayout.hint = getString(R.string.phone_number)
            itemPhone.edText.setPhoneNumInput()
        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            owner = this@ProfileDetailView,
            factory = ProfileDetailViewInjector(requireActivity().application).provideViewModelFactory()
        )[ProfileDetailViewModel::class.java]
        viewModel.handleEvent(UserEvent.GetCurrentUser)
    }

    private fun ProfileDetailViewModel.startObserving() {
        loading.observe(viewLifecycleOwner) {
            viewInteract.updateProgressLoad(it)
        }
        error.observe(viewLifecycleOwner) {
            viewInteract.displayToast(it.asString(context))
        }
        updated.observe(viewLifecycleOwner) {
            viewInteract.restartHomeActivity()
        }
        nameTxtValidateState.observe(viewLifecycleOwner) {
            binding?.itemName?.textInputLayout?.error = getString(R.string.invalid)
        }
        user.observe(viewLifecycleOwner) {
            binding?.apply {
                itemName.edText.text = it?.name?.toEditable()
                itemPhone.edText.text = it?.phone?.toEditable()
            }
        }
    }
}
