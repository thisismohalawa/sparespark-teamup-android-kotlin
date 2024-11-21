package sparespark.teamup.productlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetBehavior
import sparespark.teamup.R
import sparespark.teamup.core.base.BaseExpandedListView
import sparespark.teamup.core.bindCompanyList
import sparespark.teamup.core.binding.ViewBindingHolder
import sparespark.teamup.core.binding.ViewBindingHolderImpl
import sparespark.teamup.core.isMatch
import sparespark.teamup.core.relaunchCurrentView
import sparespark.teamup.core.selectQuery
import sparespark.teamup.core.setupListItemDecoration
import sparespark.teamup.core.toEditable
import sparespark.teamup.core.visible
import sparespark.teamup.core.wrapper.EventObserver
import sparespark.teamup.data.model.product.Product
import sparespark.teamup.databinding.ExpandedListViewBinding
import sparespark.teamup.home.HomeActivityInteract
import sparespark.teamup.productlist.buildlogic.ProductViewInjector

class ProductView : BaseExpandedListView(), View.OnClickListener,
    ViewBindingHolder<ExpandedListViewBinding> by ViewBindingHolderImpl() {

    private lateinit var productAdapter: ProductAdapter
    private lateinit var viewModel: ProductViewModel
    private lateinit var viewInteract: HomeActivityInteract
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>

    override fun onDestroyView() {
        super.onDestroyView()
        destroyBinding()
    }

    override fun onClick(p0: View?) {
        if (p0?.id == R.id.txt_update) viewModel.handleEvent(
            ProductEvent.OnUpdateTxtClick(
                product = binding?.edTitle?.text?.trim().toString()
            )
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = initBinding(ExpandedListViewBinding.inflate(layoutInflater), this@ProductView) {
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
            edTitle.hint = getString(R.string.product)
            itemSearch.mtSearchView.queryHint = getString(R.string.search_products)
            itemShareClient.root.visible(false)
        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            owner = this@ProductView,
            factory = ProductViewInjector(requireActivity().application).provideViewModelFactory()
        )[ProductViewModel::class.java]
        viewModel.handleEvent(ProductEvent.GetProductList)
        viewModel.handleEvent(ProductEvent.OnStartGetProduct)
    }

    private fun setupClickListener() {
        binding?.txtUpdate?.setOnClickListener(this@ProductView)
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            if (bottomSheetBehavior.state != BottomSheetBehavior.STATE_HIDDEN) {
                viewModel.handleEvent(
                    ProductEvent.HideBottomSheet
                )
            } else {
                findNavController().popBackStack()
            }
        }
    }

    private fun setupListAdapter() {
        productAdapter = ProductAdapter()
        binding?.recItemList?.apply {
            adapter = productAdapter
            setupListItemDecoration(context)
        }
        productAdapter.event.observe(viewLifecycleOwner) {
            viewModel.handleEvent(it)
        }
    }

    private fun setupSearchViewListener(list: List<Product>) {
        binding?.itemSearch?.mtSearchView?.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    val filteredList = mutableListOf<Product>()
                    for (product: Product in list) if (product.name.isMatch(it)) filteredList.add(
                        product
                    )

                    if (filteredList.isNotEmpty()) productAdapter.submitList(filteredList)
                }
                return true
            }
        })
    }

    private fun bindProduct(product: Product) {
        binding?.apply {
            edTitle.text = product.name.toEditable()
            dataSpinner.selectQuery(query = product.companyEntry.companyName)
        }
    }

    private fun ProductViewModel.setupStatesObserver() {
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
        product.observe(viewLifecycleOwner) {
            bindProduct(it)
        }
        productList.observe(viewLifecycleOwner) {
            productAdapter.submitList(it)
            setupSearchViewListener(it)
        }
        /*===============*/
        companyList.observe(viewLifecycleOwner) {
            binding?.dataSpinner?.bindCompanyList(
                list = it,
                selectTitle = null,
                selectAction = { position ->
                    handleEvent(ProductEvent.OnSpinnerCompanySelect(position ?: 99))
                })
        }
        addCompanyNavigateAttempt.observe(viewLifecycleOwner, EventObserver {
            viewInteract.displaySnack(
                msg = getString(R.string.empty_company_list),
                aMsg = getString(R.string.add),
                action = {
                    if (findNavController().currentDestination?.id == R.id.productView) findNavController().navigate(
                        ProductViewDirections.navigateToCompany()
                    )
                }
            )
        })
    }
}