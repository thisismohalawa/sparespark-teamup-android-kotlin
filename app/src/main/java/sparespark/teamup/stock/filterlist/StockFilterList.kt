package sparespark.teamup.stock.filterlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import sparespark.teamup.R
import sparespark.teamup.core.bindCityList
import sparespark.teamup.core.bindClientList
import sparespark.teamup.core.bindCompanyList
import sparespark.teamup.core.bindProductList
import sparespark.teamup.core.binding.ViewBindingHolder
import sparespark.teamup.core.binding.ViewBindingHolderImpl
import sparespark.teamup.core.displayDatePicker
import sparespark.teamup.core.relaunchCurrentView
import sparespark.teamup.core.setClickListenerWithViewDelayEnabled
import sparespark.teamup.core.setCustomImage
import sparespark.teamup.core.setupListItemStaggeredView
import sparespark.teamup.core.toCalendarSearchDay
import sparespark.teamup.core.visible
import sparespark.teamup.core.wrapper.EventObserver
import sparespark.teamup.databinding.FilterlistViewBinding
import sparespark.teamup.home.HomeActivityInteract
import sparespark.teamup.stock.BaseStockListView
import sparespark.teamup.stock.filterlist.buildlogic.StockFilterViewModelInjector
import sparespark.teamup.stock.itemlist.adapter.StockListAdapter
import sparespark.teamup.stock.itemlist.adapter.StockStaticsAdapter

class StockFilterList : BaseStockListView(),
    ViewBindingHolder<FilterlistViewBinding> by ViewBindingHolderImpl() {

    private lateinit var viewInteract: HomeActivityInteract
    private lateinit var viewModel: StockFilterViewModel
    private lateinit var stockAdapter: StockListAdapter
    private lateinit var stockStaticsAdapter: StockStaticsAdapter

    override fun onDestroyView() {
        super.onDestroyView()
        destroyBinding()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View =
        initBinding(FilterlistViewBinding.inflate(layoutInflater), this@StockFilterList) {
            setupViewInteract()
            setupViewInput()
            setupListAdapter()
            setupStaticsListAdapter()
            setupViewModel()
            viewModel.setupStateObserver()
            setupSearchViewListener()
            setupViewClickListener()
        }

    private fun setupViewInteract() {
        viewInteract = activity as HomeActivityInteract
    }

    private fun setupViewInput() {
        binding?.apply {
            btnSearch.text = getString(R.string.search)
            itemSearch.mtSearchView.queryHint = getString(R.string.search_by_date)
            itemHeaderList.apply {
                itemHeader.txtTitle.text = getString(R.string.stock)
                imgAction.setCustomImage(R.drawable.ic_edit, context)
            }
            itemBalanceList.root.visible(isVisible = false)

        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            owner = this@StockFilterList,
            factory = StockFilterViewModelInjector(requireActivity().application).provideViewModelFactory()
        )[StockFilterViewModel::class.java]
        viewModel.handleViewEvent(StockFilterListEvent.OnViewStart)
    }

    private fun setupListAdapter() {
        stockAdapter = StockListAdapter(isExpanded = true)
        binding?.recItemList?.apply {
            setupListItemStaggeredView()
            adapter = stockAdapter
        }
        stockAdapter.event.observe(viewLifecycleOwner) {
            viewModel.handleEvent(it)
        }
    }

    private fun setupStaticsListAdapter() {
        stockStaticsAdapter = StockStaticsAdapter()
        binding?.itemStaticsList?.recStaticsList?.adapter = stockStaticsAdapter
    }

    private fun setupViewClickListener() {
        binding?.apply {
            btnSearch.setOnClickListener {
                viewModel.handleViewEvent(StockFilterListEvent.OnFilterBtnClick)
            }


            itemHeaderList.imgAction.setClickListenerWithViewDelayEnabled {
                displayDatePicker(
                    fragmentManager = activity?.supportFragmentManager,
                    title = getString(R.string.date),
                    tag = "Ab",
                    action = {
                        itemSearch.mtSearchView.setQuery(toCalendarSearchDay(it), false)
                    }
                )
            }
        }
    }

    private fun setupSearchViewListener() {
        binding?.itemSearch?.mtSearchView?.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false

            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.handleViewEvent(
                    StockFilterListEvent.OnSearchQueryTextUpdate(
                        newText.toString()
                    )
                )
                return true
            }
        })
    }

    private fun isDestination(): Boolean =
        findNavController().currentDestination?.id == R.id.stockFilterList

    private fun StockFilterViewModel.setupStateObserver() {
        loading.observe(viewLifecycleOwner) {
            viewInteract.updateProgressLoad(it)
        }
        error.observe(viewLifecycleOwner) {
            viewInteract.displayToast(it.asString(context))
        }
        updatedStock.observe(viewLifecycleOwner, EventObserver {
            relaunchCurrentView()
        })
        /*==========*/
        stockList.observe(viewLifecycleOwner) {
            stockAdapter.submitList(it)
        }
        editIStock.observe(viewLifecycleOwner, EventObserver {
            if (isDestination()) findNavController().navigate(
                StockFilterListDirections.navigateToStockDetails(
                    itemId = it
                )
            )
        })
        /*============================*/
        updateItemListHintAttempt.observe(viewLifecycleOwner) {
            binding?.itemHeaderList?.itemHeader?.txtHint?.updateHintTitle(it)
        }
        updatePositionSelectListAttempt.observe(viewLifecycleOwner) {
            binding?.recItemList?.updateSelectItem(it)
        }
        /*============================*/
        useStockStaticsState.observe(viewLifecycleOwner) {
            binding?.itemStaticsList?.root?.visible(it)
        }
        stockStaticsList.observe(viewLifecycleOwner) {
            stockStaticsAdapter.submitList(it)
        }
        /*===========================*/
        cityList.observe(viewLifecycleOwner) {
            binding?.apply {
                citySpinner.bindCityList(
                    list = it,
                    selectTitle = getString(R.string.select_city),
                    selectAction = { position ->
                        handleViewEvent(StockFilterListEvent.OnSpinnerCitySelect(position))
                    })
            }
        }
        clientList.observe(viewLifecycleOwner) {
            binding?.apply {
                clientSpinner.bindClientList(
                    list = it,
                    selectTitle = getString(R.string.select_client),
                    selectAction = { position ->
                        handleViewEvent(StockFilterListEvent.OnSpinnerClientSelect(position))
                    })
            }
        }
        companyList.observe(viewLifecycleOwner) {
            binding?.apply {
                companySpinner.bindCompanyList(
                    list = it,
                    selectTitle = getString(R.string.select_company),
                    selectAction = { position ->
                        handleViewEvent(StockFilterListEvent.OnSpinnerCompanySelect(position))
                    })
            }
        }
        productList.observe(viewLifecycleOwner) {
            binding?.apply {
                productSpinner.bindProductList(
                    list = it,
                    selectTitle = getString(R.string.select_product),
                    selectAction = { position ->
                        handleViewEvent(StockFilterListEvent.OnSpinnerProductSelect(position))
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
