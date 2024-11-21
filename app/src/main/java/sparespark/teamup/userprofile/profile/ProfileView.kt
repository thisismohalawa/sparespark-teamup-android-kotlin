package sparespark.teamup.userprofile.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import sparespark.teamup.R
import sparespark.teamup.core.binding.ViewBindingHolder
import sparespark.teamup.core.binding.ViewBindingHolderImpl
import sparespark.teamup.core.menu.MENU_CITY
import sparespark.teamup.core.menu.MENU_CLIENT
import sparespark.teamup.core.menu.MENU_COMPANY
import sparespark.teamup.core.menu.MENU_PRODUCT
import sparespark.teamup.core.menu.MENU_TEAM
import sparespark.teamup.core.setupListItemDecoration
import sparespark.teamup.core.wrapper.EventObserver
import sparespark.teamup.data.model.ProfileMenu
import sparespark.teamup.databinding.ProfileViewBinding
import sparespark.teamup.home.HomeActivityInteract
import sparespark.teamup.userprofile.UserEvent
import sparespark.teamup.userprofile.profile.buildlogic.ProfileViewInjector

class ProfileView : Fragment(), ViewBindingHolder<ProfileViewBinding> by ViewBindingHolderImpl() {

    private lateinit var menuAdapter: MenuAdapter
    private lateinit var viewModel: ProfileViewModel
    private lateinit var viewInteract: HomeActivityInteract

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
        setupClickListener()
    }

    private fun setupViewInteract() {
        viewInteract = activity as HomeActivityInteract
    }

    private fun setupMenuAdapter(list: List<ProfileMenu>) {
        menuAdapter = MenuAdapter(list = list)
        menuAdapter.event.observe(viewLifecycleOwner) {
            viewModel.handleEvent(it)
        }
        binding?.recMenuList?.apply {
            setupListItemDecoration(context)
            adapter = menuAdapter
        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            owner = this@ProfileView,
            factory = ProfileViewInjector(requireActivity().application).provideViewModelFactory()
        )[ProfileViewModel::class.java]
        viewModel.handleEvent(UserEvent.GetCurrentUser)
    }

    private fun setupClickListener() {
        val userListener = View.OnClickListener {
            findNavController().navigate(ProfileViewDirections.navigateToProfileDetailView())
        }
        binding?.itemHeaderProfile?.apply {
            txtName.setOnClickListener(userListener)
            txtEmail.setOnClickListener(userListener)
            imgUser.setOnClickListener(userListener)
        }
    }

    private fun isProfileView(): Boolean =
        findNavController().currentDestination?.id == R.id.profileView

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
        menuList.observe(viewLifecycleOwner) {
            setupMenuAdapter(it)
        }
        user.observe(viewLifecycleOwner) {
            binding?.itemHeaderProfile?.apply {
                txtName.text = it?.name
                txtEmail.text = it?.email
            }
        }
        editMenu.observe(viewLifecycleOwner, EventObserver {
            if (isProfileView()) when (it) {
                MENU_CITY -> findNavController().navigate(ProfileViewDirections.navigateToCity())
                MENU_CLIENT -> findNavController().navigate(ProfileViewDirections.navigateToClient())
                MENU_COMPANY -> findNavController().navigate(ProfileViewDirections.navigateToCompany())
                MENU_PRODUCT -> findNavController().navigate(ProfileViewDirections.navigateToProduct())
                MENU_TEAM -> findNavController().navigate(ProfileViewDirections.navigateToTeam())
            }
        })
    }
}