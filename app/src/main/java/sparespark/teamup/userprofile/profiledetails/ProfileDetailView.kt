package sparespark.teamup.userprofile.profiledetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import sparespark.teamup.R
import sparespark.teamup.core.MAX_INPUT_NAME_DIG
import sparespark.teamup.core.MIN_TITLE_DIG
import sparespark.teamup.core.beginLayoutTitleLengthWatcher
import sparespark.teamup.core.binding.ViewBindingHolder
import sparespark.teamup.core.binding.ViewBindingHolderImpl
import sparespark.teamup.core.setPhoneNumInput
import sparespark.teamup.core.toEditable
import sparespark.teamup.core.visible
import sparespark.teamup.data.model.User
import sparespark.teamup.databinding.InputsViewBinding
import sparespark.teamup.home.HomeViewInteract
import sparespark.teamup.userprofile.UserEvent
import sparespark.teamup.userprofile.profiledetails.buildlogic.ProfileDetailViewInjector

class ProfileDetailView : Fragment(),
    View.OnClickListener,
    ViewBindingHolder<InputsViewBinding> by ViewBindingHolderImpl() {

    private lateinit var viewModel: ProfileDetailViewModel
    private lateinit var viewInteract: HomeViewInteract

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = initBinding(InputsViewBinding.inflate(layoutInflater), this@ProfileDetailView) {
        setupViewInteract()
        customizeView()
        setupViewModel()
        viewModel.startObserving()
        btnAction.setOnClickListener(this@ProfileDetailView)
    }

    private fun setupViewInteract() {
        viewInteract = activity as HomeViewInteract
    }

    private fun customizeView() {
        binding?.apply {
            btnAction.text = getString(R.string.update)
            itemInput1.apply {
                textInputLayout.hint = getString(R.string.name) + getString(R.string.required)
                edText.beginLayoutTitleLengthWatcher(
                    inputLayout = itemInput1.textInputLayout,
                    minDig = MIN_TITLE_DIG,
                    maxDig = MAX_INPUT_NAME_DIG,
                    eMsg = getString(R.string.invalid)
                )
            }
            itemInput2.apply {
                textInputLayout.hint = getString(R.string.phone_number)
                edText.setPhoneNumInput()
            }
        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            owner = this@ProfileDetailView,
            factory = ProfileDetailViewInjector(requireActivity().application).provideViewModelFactory()
        )[ProfileDetailViewModel::class.java]
        viewModel.handleEvent(UserEvent.GetUser)
    }

    private fun ProfileDetailViewModel.startObserving() {
        loading.observe(viewLifecycleOwner) {
            binding?.progressBar?.visible(it)
        }
        error.observe(viewLifecycleOwner) {
            viewInteract.displayToast(it.asString(context))
        }
        updated.observe(viewLifecycleOwner) {
            viewInteract.restartHomeActivity()
        }
        nameTxtValidateState.observe(viewLifecycleOwner) {
            binding?.itemInput1?.textInputLayout?.error = getString(R.string.invalid)
        }
        user.observe(viewLifecycleOwner) {
            bindUser(it)
        }
    }

    private fun bindUser(user: User?) {
        binding?.apply {
            itemInput1.edText.text = user?.name?.toEditable()
            itemInput2.edText.text = user?.phone?.toEditable()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        destroyBinding()
    }

    override fun onClick(p0: View?) {
        if (p0?.id == R.id.btn_action) viewModel.handleEvent(
            UserEvent.OnUpdateBtnClick(
                name = binding?.itemInput1?.edText?.text?.trim().toString(),
                phone = binding?.itemInput2?.edText?.text?.trim().toString()
            )
        )
    }
}