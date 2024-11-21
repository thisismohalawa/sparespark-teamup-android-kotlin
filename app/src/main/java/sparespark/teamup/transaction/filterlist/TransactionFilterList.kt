package sparespark.teamup.transaction.filterlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import sparespark.teamup.R
import sparespark.teamup.core.bindCityList
import sparespark.teamup.core.bindClientList
import sparespark.teamup.core.binding.ViewBindingHolder
import sparespark.teamup.core.binding.ViewBindingHolderImpl
import sparespark.teamup.core.displayDatePicker
import sparespark.teamup.core.setCustomImage
import sparespark.teamup.core.toCalendarSearchDay
import sparespark.teamup.core.visible
import sparespark.teamup.core.wrapper.EventObserver
import sparespark.teamup.databinding.FilterlistViewBinding
import sparespark.teamup.home.HomeActivityInteract
import sparespark.teamup.transaction.BaseTransactionView
import sparespark.teamup.transaction.filterlist.buildlogic.TransactionFilterListInjector
import sparespark.teamup.transaction.itemlist.adapter.TransactionAdapter
import sparespark.teamup.transaction.itemlist.adapter.TransactionBalanceAdapter

class TransactionFilterList : BaseTransactionView(),
    ViewBindingHolder<FilterlistViewBinding> by ViewBindingHolderImpl() {

    private lateinit var itemAdapter: TransactionAdapter
    private lateinit var balanceAdapter: TransactionBalanceAdapter
    private lateinit var viewModel: TransactionFilterListViewModel
    private lateinit var viewInteract: HomeActivityInteract

    override fun onDestroyView() {
        super.onDestroyView()
        destroyBinding()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View =
        initBinding(FilterlistViewBinding.inflate(layoutInflater), this@TransactionFilterList) {
            setupViewInteract()
            setupViewInputs()
            setupViewModel()
            setupListAdapter()
            setupBalanceListAdapter()
            viewModel.setupItemStatesObserver()
            setupSearchViewListener()
            setupClickListener()
        }

    private fun setupViewInteract() {
        viewInteract = activity as HomeActivityInteract
    }

    private fun setupViewInputs() {
        binding?.apply {
            btnSearch.text = getString(R.string.search)
            itemSearch.mtSearchView.queryHint = getString(R.string.search_by_date)
            itemHeaderList.apply {
                itemHeader.txtTitle.text = getString(R.string.transactions)
                imgAction.setCustomImage(R.drawable.ic_settings, context)
                imgSubAction.setCustomImage(R.drawable.ic_edit, context)
            }

            itemBalanceList.apply {
                itemHeader.root.visible(isVisible = false)
                imgBalancePref.visible(isVisible = false)
            }

            itemStaticsList.root.visible(isVisible = false)
        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            owner = this@TransactionFilterList,
            factory = TransactionFilterListInjector(requireActivity().application).provideViewModelFactory()
        )[TransactionFilterListViewModel::class.java]
        viewModel.handleViewEvent(TransactionFilterListEvent.OnViewStart)
    }

    private fun setupSearchViewListener() {
        binding?.itemSearch?.mtSearchView?.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false

            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.handleViewEvent(
                    TransactionFilterListEvent.OnSearchQueryTextUpdate(
                        newText.toString()
                    )
                )
                return true
            }
        })
    }

    private fun setupListAdapter() {
        itemAdapter = TransactionAdapter(isExpanded = true)
        binding?.recItemList?.apply {
            adapter = itemAdapter
        }
        itemAdapter.event.observe(viewLifecycleOwner) {
            viewModel.handleEvent(it)
        }
    }

    private fun setupBalanceListAdapter() {
        balanceAdapter = TransactionBalanceAdapter(isHorizontalBalanceView = true)
        binding?.itemBalanceList?.recBalanceList?.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = balanceAdapter
        }
    }

    private fun setupClickListener() {
        binding?.apply {
            itemHeaderList.imgAction.setOnClickListener {
                navigateToListPreference()
            }
            itemHeaderList.imgSubAction.setOnClickListener {
                displayDatePicker(
                    fragmentManager = activity?.supportFragmentManager,
                    title = getString(R.string.date),
                    tag = "A",
                    action = { cal ->
                        itemSearch.mtSearchView.setQuery(
                            toCalendarSearchDay(cal), false
                        )
                    }
                )
            }
            btnSearch.setOnClickListener {
                viewModel.handleViewEvent(TransactionFilterListEvent.OnTransactionFilterBtnClick)
            }
        }
    }

    private fun isDestination(): Boolean =
        findNavController().currentDestination?.id == R.id.transactionFilterList

    private fun navigateTransactionDetailsView(transactionId: String) =
        if (isDestination()) findNavController().navigate(
            TransactionFilterListDirections.navigateToTransactionDetails(
                itemId = transactionId
            )
        ) else Unit

    private fun navigateToListPreference() =
        if (isDestination()) findNavController().navigate(
            TransactionFilterListDirections.navigateToFilterListPreference()
        ) else Unit

    private fun navigateToTransactionList() =
        if (isDestination()) findNavController().navigate(
            TransactionFilterListDirections.navigateToTransactionList()
        ) else Unit

    private fun TransactionFilterListViewModel.setupItemStatesObserver() {
        loading.observe(viewLifecycleOwner) {
            viewInteract.updateProgressLoad(it)
        }
        error.observe(viewLifecycleOwner) {
            viewInteract.displayToast(it.asString(context))
        }
        updated.observe(viewLifecycleOwner, EventObserver {
            navigateToTransactionList()
        })
        /*=============*/
        searchQueryTextState.observe(viewLifecycleOwner) {
            binding?.itemSearch?.mtSearchView?.setQuery(it, false)
        }
        /*===============*/
        itemList.observe(viewLifecycleOwner) {
            itemAdapter.submitList(it)
        }
        editItem.observe(viewLifecycleOwner, EventObserver {
            navigateTransactionDetailsView(it)
        })
        /*=============*/
        updateItemListHintAttempt.observe(viewLifecycleOwner) {
            binding?.itemHeaderList?.itemHeader?.txtHint?.updateHintTitle(it)
        }
        updatePositionSelectListAttempt.observe(viewLifecycleOwner) {
            binding?.recItemList?.updateSelectItem(it)
        }
        /*============================*/
        useBalanceState.observe(viewLifecycleOwner) {
            binding?.itemBalanceList?.root?.visible(it)
        }
        balanceList.observe(viewLifecycleOwner) {
            balanceAdapter.submitList(it)
        }
        /*=============*/
        cityList.observe(viewLifecycleOwner) {
            binding?.apply {
                citySpinner.bindCityList(list = it,
                    selectTitle = getString(R.string.select_city),
                    selectAction = { position ->
                        clientSpinner.setSelection(0)
                        handleViewEvent(TransactionFilterListEvent.OnSpinnerCitySelect(position))
                    })
            }
        }
        clientList.observe(viewLifecycleOwner) {
            binding?.apply {
                clientSpinner.bindClientList(list = it,
                    selectTitle = getString(R.string.select_client),
                    selectAction = { position ->
                        citySpinner.setSelection(0)
                        handleViewEvent(TransactionFilterListEvent.OnSpinnerClientSelect(position))
                    })
            }
        }

        /*============================*/
        exported.observe(viewLifecycleOwner) {
            viewInteract.displayToast(getString(R.string.export_success))
        }
        copyItemListAttempt.observe(viewLifecycleOwner, EventObserver {
            if (it != null) viewInteract.actionCopyText(it)
        })
        shareItemListAttempt.observe(viewLifecycleOwner, EventObserver {
            if (it != null) viewInteract.actionShareText(it)
        })
    }
}
