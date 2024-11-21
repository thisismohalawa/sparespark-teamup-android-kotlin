package sparespark.teamup.companylist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetBehavior
import sparespark.teamup.R
import sparespark.teamup.companylist.buildlogic.CompanyViewInjector
import sparespark.teamup.core.base.BaseExpandedListView
import sparespark.teamup.core.binding.ViewBindingHolder
import sparespark.teamup.core.binding.ViewBindingHolderImpl
import sparespark.teamup.core.isMatch
import sparespark.teamup.core.relaunchCurrentView
import sparespark.teamup.core.setupListItemDecoration
import sparespark.teamup.core.toEditable
import sparespark.teamup.core.visible
import sparespark.teamup.data.model.company.Company
import sparespark.teamup.databinding.ExpandedListViewBinding
import sparespark.teamup.home.HomeActivityInteract

class CompanyView : BaseExpandedListView(), View.OnClickListener,
    ViewBindingHolder<ExpandedListViewBinding> by ViewBindingHolderImpl() {

    private lateinit var companyAdapter: CompanyAdapter
    private lateinit var viewModel: CompanyViewModel
    private lateinit var viewInteract: HomeActivityInteract
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>

    override fun onDestroyView() {
        super.onDestroyView()
        destroyBinding()
    }

    override fun onClick(p0: View?) {
        if (p0?.id == R.id.txt_update) viewModel.handleEvent(
            CompanyEvent.OnUpdateTxtClick(
                company = binding?.edTitle?.text?.trim().toString()
            )
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = initBinding(ExpandedListViewBinding.inflate(layoutInflater), this@CompanyView) {
        setupBottomSheetState()
        setupViewInteract()
        setupViewInputs()
        setupListAdapter()
        setupViewModel()
        viewModel.setupStatesObserver()
        setupClickListener()
    }

    private fun setupBottomSheetState() {
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
            edTitle.hint = getString(R.string.company)
            itemSearch.mtSearchView.queryHint = getString(R.string.search_companies)
            itemShareClient.root.visible(false)
        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            owner = this@CompanyView,
            factory = CompanyViewInjector(requireActivity().application).provideViewModelFactory()
        )[CompanyViewModel::class.java]
        viewModel.handleEvent(CompanyEvent.GetCompanyList)
        viewModel.handleEvent(CompanyEvent.OnStartGetCompany)
    }

    private fun setupClickListener() {
        binding?.txtUpdate?.setOnClickListener(this@CompanyView)
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            if (bottomSheetBehavior.state != BottomSheetBehavior.STATE_HIDDEN) {
                viewModel.handleEvent(
                    CompanyEvent.HideBottomSheet
                )
            } else {
                findNavController().popBackStack()
            }
        }
    }

    private fun setupListAdapter() {
        companyAdapter = CompanyAdapter()
        binding?.recItemList?.apply {
            adapter = companyAdapter
            setupListItemDecoration(context)
        }
        companyAdapter.event.observe(viewLifecycleOwner) {
            viewModel.handleEvent(it)
        }
    }

    private fun setupSearchViewListener(list: List<Company>) {
        binding?.itemSearch?.mtSearchView?.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    val filteredList = mutableListOf<Company>()
                    for (company: Company in list)
                        if (company.name.isMatch(it))
                            filteredList.add(company)

                    if (filteredList.isNotEmpty()) companyAdapter.submitList(filteredList)
                }
                return true
            }
        })
    }

    private fun CompanyViewModel.setupStatesObserver() {
        loading.observe(viewLifecycleOwner) {
            viewInteract.updateProgressLoad(it)
        }
        error.observe(viewLifecycleOwner) {
            viewInteract.displayToast(it.asString(context))
        }
        updated.observe(viewLifecycleOwner) {
            relaunchCurrentView()
        }
        updatePositionSelectListAttempt.observe(viewLifecycleOwner) {
            binding?.recItemList?.updateSelectItem(it)
        }
        bottomSheetViewState.observe(viewLifecycleOwner) {
            bottomSheetBehavior.state = it
        }
        company.observe(viewLifecycleOwner) {
            binding?.edTitle?.text = it.name.toEditable()
        }
        companyList.observe(viewLifecycleOwner) {
            companyAdapter.submitList(it)
            setupSearchViewListener(it)
        }
    }
}