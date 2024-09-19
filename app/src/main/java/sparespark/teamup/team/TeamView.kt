package sparespark.teamup.team

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import sparespark.teamup.core.binding.ViewBindingHolder
import sparespark.teamup.core.binding.ViewBindingHolderImpl
import sparespark.teamup.core.relaunchCurrentView
import sparespark.teamup.core.setupListItemDecoration
import sparespark.teamup.databinding.TeamlistViewBinding
import sparespark.teamup.home.HomeViewInteract
import sparespark.teamup.home.base.BaseViewBehavioral
import sparespark.teamup.team.buildlogic.TeamViewInjector

class TeamView : Fragment(),
    BaseViewBehavioral,
    ViewBindingHolder<TeamlistViewBinding> by ViewBindingHolderImpl() {

    private lateinit var teamAdapter: TeamAdapter
    private lateinit var viewModel: TeamViewModel
    private lateinit var viewInteract: HomeViewInteract

    override fun onDestroyView() {
        super.onDestroyView()
        destroyBinding()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = initBinding(TeamlistViewBinding.inflate(layoutInflater), this@TeamView) {
        setupViewInteract()
        setupListAdapter()
        setupViewModel()
        viewModel.setupStatesObserver()
    }

    override fun setupViewInteract() {
        viewInteract = activity as HomeViewInteract
    }

    override fun setupViewModel() {
        viewModel = ViewModelProvider(
            owner = this@TeamView,
            factory = TeamViewInjector(requireActivity().application).provideTeamListViewModelFactory()
        )[TeamViewModel::class.java]
        viewModel.handleEvent(TeamEvent.GetTeam)
    }

    override fun setupViewInputs() = Unit

    override fun setupClickListener() = Unit

    private fun setupListAdapter() {
        teamAdapter = TeamAdapter()
        binding?.recTeamList?.apply {
            setupListItemDecoration(context)
            adapter = teamAdapter
        }
        teamAdapter.event.observe(
            viewLifecycleOwner
        ) {
            viewModel.handleEvent(it)
        }
    }

    private fun TeamViewModel.setupStatesObserver() {
        loading.observe(viewLifecycleOwner) {
            viewInteract.updateProgressLoad(it)
        }
        error.observe(viewLifecycleOwner) {
            viewInteract.displayToast(it.asString(context))
        }
        updated.observe(viewLifecycleOwner) {
            relaunchCurrentView()
        }
        teamList.observe(viewLifecycleOwner) {
            teamAdapter.submitList(it)
        }
    }
}