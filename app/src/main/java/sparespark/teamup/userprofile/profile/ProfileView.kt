package sparespark.teamup.userprofile.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import sparespark.teamup.R
import sparespark.teamup.core.MENU_CITY
import sparespark.teamup.core.MENU_CLIENT
import sparespark.teamup.core.MENU_PROFILE
import sparespark.teamup.core.MENU_TEAM
import sparespark.teamup.core.binding.ViewBindingHolder
import sparespark.teamup.core.binding.ViewBindingHolderImpl
import sparespark.teamup.core.setupListItemDecoration
import sparespark.teamup.core.wrapper.EventObserver
import sparespark.teamup.data.model.IMenu
import sparespark.teamup.data.model.User
import sparespark.teamup.databinding.ProfileViewBinding
import sparespark.teamup.home.HomeViewInteract
import sparespark.teamup.userprofile.UserEvent
import sparespark.teamup.userprofile.profile.buildlogic.ProfileViewInjector


class ProfileView : Fragment(),
    ViewBindingHolder<ProfileViewBinding> by ViewBindingHolderImpl() {

    private lateinit var menuAdapter: MenuAdapter
    private lateinit var viewModel: ProfileViewModel
    private lateinit var viewInteract: HomeViewInteract

    override fun onDestroyView() {
        super.onDestroyView()
        destroyBinding()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = initBinding(ProfileViewBinding.inflate(layoutInflater), this@ProfileView) {
        setupViewInteract()
        setupViewModel()
        viewModel.startObserving()
    }


    private fun setupViewInteract() {
        viewInteract = activity as HomeViewInteract
    }

    private fun setupMenuAdapter(list: List<IMenu>) {
        menuAdapter = MenuAdapter(list = list)
        menuAdapter.event.observe(
            viewLifecycleOwner
        ) {
            viewModel.handleEvent(it)
        }
        binding?.recSettingsList?.apply {
            setupListItemDecoration(context)
            adapter = menuAdapter
        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            owner = this@ProfileView,
            factory = ProfileViewInjector(requireActivity().application).provideViewModelFactory()
        )[ProfileViewModel::class.java]
        viewModel.handleEvent(UserEvent.GetUser)
    }

    private fun ProfileViewModel.startObserving() {
        loading.observe(viewLifecycleOwner) {
            viewInteract.updateProgressLoad(it)
        }
        error.observe(viewLifecycleOwner) {
            viewInteract.displayToast(it.asString(context))
        }
        updated.observe(viewLifecycleOwner) {
            viewInteract.restartHomeActivity()
        }
        loginAttempt.observe(viewLifecycleOwner) {
            viewInteract.startAuthActivity()
        }
        user.observe(viewLifecycleOwner) {
            bindUser(it)
        }
        menuList.observe(viewLifecycleOwner) {
            setupMenuAdapter(it)
        }
        editMenu.observe(viewLifecycleOwner, EventObserver {
            when (it) {
                MENU_CITY -> navigateToCityView()
                MENU_CLIENT -> navigateToClientView()
                MENU_TEAM -> navigateToTeamView()
                MENU_PROFILE -> navigateToProfileDetailView()
            }
        })
    }

    private fun bindUser(user: User?) {
        binding?.itemHeaderProfile?.apply {
            txtName.text = user?.name
            txtEmail.text = user?.email
        }
    }

    private fun isProfileView(): Boolean =
        findNavController().currentDestination?.id == R.id.profileView

    private fun navigateToProfileDetailView() = if (isProfileView()) findNavController().navigate(
        ProfileViewDirections.navigateToProfileDetailView()
    ) else Unit

    private fun navigateToCityView() = if (isProfileView()) findNavController().navigate(
        ProfileViewDirections.navigateToCity()
    ) else Unit

    private fun navigateToTeamView() = if (isProfileView()) findNavController().navigate(
        ProfileViewDirections.navigateToTeamView()
    ) else Unit

    private fun navigateToClientView() = if (isProfileView()) findNavController().navigate(
        ProfileViewDirections.navigateToClient()
    ) else Unit
}