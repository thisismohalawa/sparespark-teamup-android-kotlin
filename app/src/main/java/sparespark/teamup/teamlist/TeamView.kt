package sparespark.teamup.teamlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetBehavior
import sparespark.teamup.R
import sparespark.teamup.core.binding.ViewBindingHolder
import sparespark.teamup.core.binding.ViewBindingHolderImpl
import sparespark.teamup.core.isMatch
import sparespark.teamup.core.relaunchCurrentView
import sparespark.teamup.core.setupListItemDecoration
import sparespark.teamup.core.visible
import sparespark.teamup.data.model.team.Team
import sparespark.teamup.databinding.ExpandedListViewBinding
import sparespark.teamup.home.HomeActivityInteract
import sparespark.teamup.teamlist.buildlogic.TeamViewInjector

class TeamView : Fragment(),
    ViewBindingHolder<ExpandedListViewBinding> by ViewBindingHolderImpl() {

    private lateinit var teamAdapter: TeamAdapter
    private lateinit var viewModel: TeamViewModel
    private lateinit var viewInteract: HomeActivityInteract
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>

    override fun onDestroyView() {
        super.onDestroyView()
        destroyBinding()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = initBinding(ExpandedListViewBinding.inflate(layoutInflater), this@TeamView) {
        setupBottomSheet()
        setupViewInteract()
        setupViewInputs()
        setupListAdapter()
        setupViewModel()
        viewModel.setupStatesObserver()
    }

    private fun setupBottomSheet() {
        binding?.apply {
            bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetSubAction)
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }
    }

    private fun setupViewInteract() {
        viewInteract = activity as HomeActivityInteract
    }

    private fun setupViewInputs() {
        binding?.apply {
            itemSearch.mtSearchView.queryHint = getString(R.string.search_for_user)
            itemShareClient.root.visible(false)
        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            owner = this@TeamView,
            factory = TeamViewInjector(requireActivity().application).provideTeamListViewModelFactory()
        )[TeamViewModel::class.java]
        viewModel.handleEvent(TeamEvent.GetTeam)
    }


    private fun setupListAdapter() {
        teamAdapter = TeamAdapter()
        binding?.recItemList?.apply {
            setupListItemDecoration(context)
            adapter = teamAdapter
        }
        teamAdapter.event.observe(viewLifecycleOwner) {
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
            setupSearchViewListener(it)
        }
    }

    private fun setupSearchViewListener(list: List<Team>) {
        binding?.itemSearch?.mtSearchView?.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    val filteredList = mutableListOf<Team>()
                    for (team: Team in list)
                        if (team.name.isMatch(it))
                            filteredList.add(team)

                    if (filteredList.isNotEmpty()) teamAdapter.submitList(filteredList)
                }
                return true
            }
        })
    }

}