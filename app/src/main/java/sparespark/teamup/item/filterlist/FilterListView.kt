package sparespark.teamup.item.filterlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import sparespark.teamup.R
import sparespark.teamup.core.DELAY_INPUT_SEARCH
import sparespark.teamup.core.binding.ViewBindingHolder
import sparespark.teamup.core.binding.ViewBindingHolderImpl
import sparespark.teamup.core.handlerPostDelayed
import sparespark.teamup.core.setCustomColor
import sparespark.teamup.core.visible
import sparespark.teamup.core.wrapper.EventObserver
import sparespark.teamup.databinding.FilterlistViewBinding
import sparespark.teamup.home.HomeViewInteract
import sparespark.teamup.home.base.BaseViewBehavioral
import sparespark.teamup.item.BaseItemsView
import sparespark.teamup.item.adapter.ItemAdapter
import sparespark.teamup.item.filterlist.buildlogic.FilterListInjector

class FilterListView : BaseItemsView(), BaseViewBehavioral,
    ViewBindingHolder<FilterlistViewBinding> by ViewBindingHolderImpl() {

    private lateinit var itemAdapter: ItemAdapter
    private lateinit var viewModel: FilterListViewModel
    private lateinit var viewInteract: HomeViewInteract

    override fun onDestroyView() {
        super.onDestroyView()
        destroyBinding()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = initBinding(FilterlistViewBinding.inflate(layoutInflater), this@FilterListView) {
        setupViewInteract()
        setupViewInputs()
        setupListAdapter()
        setupViewModel()
        viewModel.setupItemStatesObserver()
        setupClickListener()
        setupSearchViewListener()
    }

    override fun setupViewInteract() {
        viewInteract = activity as HomeViewInteract
    }

    override fun setupViewInputs() {
        binding?.apply {
            itemSearch.mtSearchView.queryHint = getString(R.string.search_items)
            itemListHeader.itemHeader.txtTitle.text = getString(R.string.exchange_list)
            itemStatics.apply {
                imgPref.visibility = View.INVISIBLE
                itemBalance.txtTitle.text = getString(R.string.filter_balance)
                itemBalance.txtTitle.setCustomColor(
                    R.color.dark, context
                )
            }
        }
    }

    override fun setupViewModel() {
        viewModel = ViewModelProvider(
            owner = this@FilterListView,
            factory = FilterListInjector(requireActivity().application).provideViewModelFactory()
        )[FilterListViewModel::class.java]
        viewModel.handleViewEvent(FilterListEvent.OnViewStart)
    }

    override fun setupClickListener() {
        binding?.apply {
            itemListHeader.imgPref.setOnClickListener {
                if (isDestination()) findNavController().navigate(FilterListViewDirections.navigateToFilterPreference())
            }
            itemStates.apply {
                radioActive.setOnCheckedChangeListener { buttonView, isChecked ->
                    if (buttonView?.isPressed == true) viewModel.handleViewEvent(FilterListEvent.OnActiveRadioBtnCheck)
                }
                radioBuy.setOnCheckedChangeListener { buttonView, isChecked ->
                    if (buttonView?.isPressed == true) viewModel.handleViewEvent(FilterListEvent.OnBuyRadioBtnCheck)
                }
                radioAdmin.setOnCheckedChangeListener { buttonView, isChecked ->
                    if (buttonView?.isPressed == true) viewModel.handleViewEvent(FilterListEvent.OnAdminRadioBtnCheck)
                }
            }
            val callback = object : OnBackPressedCallback(
                true
            ) {
                override fun handleOnBackPressed() {
                    viewModel.handleViewEvent(FilterListEvent.OnViewBackPressed)
                }
            }
            requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
        }
    }

    override fun setupListAdapter() {
        itemAdapter = ItemAdapter(isExpanded = true)
        binding?.recItemsList?.apply {
            adapter = itemAdapter
        }
        itemAdapter.event.observe(
            viewLifecycleOwner
        ) {
            viewModel.handleEvent(it)
        }
    }

    private fun setupSearchViewListener() {
        binding?.itemSearch?.mtSearchView?.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false

            override fun onQueryTextChange(newText: String?): Boolean {
                handlerPostDelayed(DELAY_INPUT_SEARCH, action = {
                    viewModel.handleViewEvent(FilterListEvent.OnSearchQueryTextUpdate(newText.toString()))
                })
                return true
            }
        })
    }

    private fun isDestination(): Boolean =
        findNavController().currentDestination?.id == R.id.filterListView

    private fun FilterListViewModel.setupItemStatesObserver() {
        exitState.observe(viewLifecycleOwner) {
            if (it) findNavController().popBackStack()
        }
        loading.observe(viewLifecycleOwner) {
            viewInteract.updateProgressLoad(it)
        }
        error.observe(viewLifecycleOwner) {
            viewInteract.displayToast(it.asString(context))
        }
        useListStaticsState.observe(viewLifecycleOwner) {
            binding?.itemStatics?.content?.visible(it)
        }
        updateItemListHintAttempt.observe(viewLifecycleOwner) {
            binding?.itemListHeader?.itemHeader?.txtHint?.updateHintTitle(it)
        }
        updateItemSelectAttempt.observe(viewLifecycleOwner) {
            binding?.recItemsList?.updateSelectItem(it)
        }
        updatedItem.observe(viewLifecycleOwner) {
            if (isDestination()) findNavController().navigate(FilterListViewDirections.navigateToItemList())
        }
        editItem.observe(viewLifecycleOwner, EventObserver {
            if (isDestination()) findNavController().navigate(
                FilterListViewDirections.navigateToItemDetailsView(
                    itemId = it
                )
            )
        })
        searchQueryTextState.observe(viewLifecycleOwner) {
            binding?.itemSearch?.mtSearchView?.setQuery(it, true)
        }
        activeListState.observe(viewLifecycleOwner) {
            binding?.itemStates?.radioActive?.isChecked = it
        }
        adminListState.observe(viewLifecycleOwner) {
            binding?.itemStates?.radioAdmin?.isChecked = it
        }
        buyListState.observe(viewLifecycleOwner) {
            binding?.itemStates?.radioBuy?.isChecked = it
        }
        listStatics.observe(viewLifecycleOwner) {
            binding?.itemStatics?.updateListStatics(it)
        }
        itemList.observe(viewLifecycleOwner) {
            itemAdapter.submitList(it)
        }
        copyItemListAttempt.observe(viewLifecycleOwner, EventObserver {
            if (it != null) viewInteract.actionCopyText(it)
        })
        shareItemListAttempt.observe(viewLifecycleOwner, EventObserver {
            if (it != null) viewInteract.actionShareText(it)
        })
        exportItemListAttempt.observe(viewLifecycleOwner, EventObserver {
            viewInteract.actionDataExport(it)
        })
    }
}