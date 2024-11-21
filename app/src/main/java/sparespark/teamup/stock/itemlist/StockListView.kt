package sparespark.teamup.stock.itemlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import sparespark.teamup.R
import sparespark.teamup.core.binding.ViewBindingHolder
import sparespark.teamup.core.binding.ViewBindingHolderImpl
import sparespark.teamup.core.relaunchCurrentView
import sparespark.teamup.core.setCustomImage
import sparespark.teamup.core.setupListItemStaggeredView
import sparespark.teamup.core.visible
import sparespark.teamup.core.wrapper.EventObserver
import sparespark.teamup.databinding.StocklistViewBinding
import sparespark.teamup.home.HomeActivityInteract
import sparespark.teamup.stock.BaseStockListView
import sparespark.teamup.stock.itemlist.adapter.StockListAdapter
import sparespark.teamup.stock.itemlist.adapter.StockStaticsAdapter
import sparespark.teamup.stock.itemlist.buildlogic.StockListInjector

class StockListView : BaseStockListView(),
    ViewBindingHolder<StocklistViewBinding> by ViewBindingHolderImpl() {

    private lateinit var viewInteract: HomeActivityInteract
    private lateinit var viewModel: StockListViewModel
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
    ): View = initBinding(StocklistViewBinding.inflate(layoutInflater), this@StockListView) {
        setupViewInteract()
        setupViewInputs()
        setupViewModel()
        setupListAdapter()
        setupStaticsListAdapter()
        viewModel.setupStateObserver()
        setupViewListener()
    }

    private fun setupViewInteract() {
        viewInteract = activity as HomeActivityInteract
    }

    private fun setupViewInputs() {
        binding?.apply {
            itemHeaderList.apply {
                itemHeader.txtTitle.text = getString(R.string.stock)
                imgAction.setCustomImage(R.drawable.ic_settings, context)
                imgSubAction.setCustomImage(R.drawable.ic_search, context)
            }
        }
    }

    private fun setupListAdapter() {
        stockAdapter = StockListAdapter()
        binding?.recStockList?.apply {
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

    private fun setupViewListener() {
        binding?.apply {
            itemHeaderList.imgAction.setOnClickListener {
                navigateToListPreference()
            }
            itemHeaderList.imgSubAction.setOnClickListener {
                navigateToFilterView()
            }
            fabAddStock.setOnClickListener {
                navigateToItemDetailsView("")
            }
            fabSellStock.setOnClickListener {
                viewModel.handleViewEvent(StockListEvent.OnSellBtnClick)
            }
        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            owner = this@StockListView,
            factory = StockListInjector(requireActivity().application).provideViewModelFactory()
        )[StockListViewModel::class.java]
        viewModel.handleViewEvent(StockListEvent.OnStartGetStockList)
    }

    private fun isDestination(): Boolean =
        findNavController().currentDestination?.id == R.id.stockListView

    private fun navigateToListPreference() = if (isDestination()) findNavController().navigate(
        StockListViewDirections.navigateToListPreference()
    ) else Unit

    private fun navigateToFilterView(
        query: String? = null
    ) = if (isDestination()) findNavController().navigate(
        StockListViewDirections.navigateToItemFilter(
            searchQuery = query
        )
    ) else Unit

    private fun navigateToItemDetailsView(itemId: String) =
        if (isDestination()) findNavController().navigate(
            StockListViewDirections.navigateToStockDetails(itemId = itemId)
        ) else Unit

    private fun navigateToItemSellView() =
        if (isDestination()) findNavController().navigate(
            StockListViewDirections.navigateToStockSell()
        ) else Unit

    private fun StockListViewModel.setupStateObserver() {
        loading.observe(viewLifecycleOwner) {
            viewInteract.updateProgressLoad(it)
        }
        error.observe(viewLifecycleOwner) {
            viewInteract.displayToast(it.asString(context))
        }
        updatedStock.observe(viewLifecycleOwner, EventObserver {
            relaunchCurrentView()
        })
        sellStockAttempt.observe(viewLifecycleOwner, EventObserver {
            navigateToItemSellView()
        })
        /*==========*/
        stockList.observe(viewLifecycleOwner) {
            stockAdapter.submitList(it)
        }
        editIStock.observe(viewLifecycleOwner, EventObserver {
            navigateToItemDetailsView(itemId = it)
        })

        /*==============================*/
        updateItemListHintAttempt.observe(viewLifecycleOwner) {
            binding?.itemHeaderList?.itemHeader?.txtHint?.updateHintTitle(it)
        }
        updatePositionSelectListAttempt.observe(viewLifecycleOwner) {
            binding?.recStockList?.updateSelectItem(it)
        }
        /*============================*/
        useStockStaticsState.observe(viewLifecycleOwner) {
            binding?.itemStaticsList?.root?.visible(it)
        }
        stockStaticsList.observe(viewLifecycleOwner) {
            stockStaticsAdapter.submitList(it)
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