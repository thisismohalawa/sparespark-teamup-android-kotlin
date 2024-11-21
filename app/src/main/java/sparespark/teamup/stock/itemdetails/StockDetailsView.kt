package sparespark.teamup.stock.itemdetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import sparespark.teamup.R
import sparespark.teamup.core.MAX_ASSET_DIG
import sparespark.teamup.core.bindStockProduct
import sparespark.teamup.core.binding.ViewBindingHolder
import sparespark.teamup.core.binding.ViewBindingHolderImpl
import sparespark.teamup.core.internal.beginInputLayoutAssetWatcher
import sparespark.teamup.core.isNewStringItem
import sparespark.teamup.core.setNumberDecimalInput
import sparespark.teamup.core.toEditable
import sparespark.teamup.core.wrapper.EventObserver
import sparespark.teamup.data.model.stock.Stock
import sparespark.teamup.databinding.StockdetailsViewBinding
import sparespark.teamup.home.HomeActivityInteract
import sparespark.teamup.stock.itemdetails.buildlogic.StockDetailsViewInjector

class StockDetailsView : Fragment(), View.OnClickListener,
    ViewBindingHolder<StockdetailsViewBinding> by ViewBindingHolderImpl() {

    private lateinit var viewModel: StockDetailsViewModel
    private lateinit var viewInteract: HomeActivityInteract

    private fun inputQuantity() = binding?.inputQuantity?.edText?.text.toString()
    private fun inputNote() = binding?.itemNote?.edText?.text?.trim().toString()

    override fun onDestroyView() {
        super.onDestroyView()
        destroyBinding()
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.btn_update_item -> viewModel.handleEvent(
                StockDetailsViewEvent.OnUpdateBtnClick(
                    quantity = inputQuantity(),
                    note = inputNote()
                )
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = initBinding(StockdetailsViewBinding.inflate(layoutInflater), this@StockDetailsView) {
        setupViewInteract()
        setupViewInputs()
        setupViewModel()
        viewModel.setupStatesObserver()
        setupClickListener()
    }

    private fun setupViewInteract() {
        viewInteract = activity as HomeActivityInteract
    }

    private fun setupViewInputs() {
        binding?.apply {
            itemNote.textInputLayout.hint = getString(R.string.note)
            itemStockProduct.textInputLayout.hint = getString(R.string.product)
            inputQuantity.apply {
                textInputLayout.hint = getString(R.string.quantity)
                edText.setNumberDecimalInput()
                edText.beginInputLayoutAssetWatcher(
                    inputLayout = textInputLayout,
                    maxDig = MAX_ASSET_DIG,
                    invalidMsg = getString(R.string.invalid)
                )
            }
        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            owner = this@StockDetailsView,
            factory = StockDetailsViewInjector(requireActivity().application).provideViewModelFactory()
        )[StockDetailsViewModel::class.java]
        viewModel.handleEvent(StockDetailsViewEvent.OnStartGetItem)
    }

    private fun setupClickListener() {
        binding?.apply {
            btnUpdateItem.setOnClickListener(this@StockDetailsView)
            tempSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
                if (buttonView?.isPressed == true) viewModel.handleEvent(
                    StockDetailsViewEvent.OnTempSwitchCheck(
                        temp = isChecked
                    )
                )
            }
        }
    }

    private fun bindStock(it: Stock) {
        binding?.apply {
            itemNote.edText.text = it.note.toEditable()
            inputQuantity.edText.text = it.assetEntry.quantity.toString().toEditable()
            itemStockProduct.edAuto.text = it.productEntry.name?.toEditable()
        }
    }

    private fun StockDetailsViewModel.setupStatesObserver() {
        loading.observe(viewLifecycleOwner) {
            viewInteract.updateProgressLoad(it)
        }
        error.observe(viewLifecycleOwner) {
            viewInteract.displayToast(it.asString(context))
        }
        updated.observe(viewLifecycleOwner) {
            viewInteract.displayToast(context?.getString(R.string.updated_success))
        }
        stock.observe(viewLifecycleOwner) {
            if (!it.productEntry.name.isNewStringItem()) bindStock(it)
        }
        /*==================*/
        quantityTextValidateState.observe(viewLifecycleOwner) {
            binding?.inputQuantity?.textInputLayout?.error = getString(R.string.invalid)
        }
        /*==================*/
        tempSwitchState.observe(viewLifecycleOwner) {
            binding?.tempSwitch?.isChecked = it
        }
        /*================*/
        shareItemAttempt.observe(viewLifecycleOwner, EventObserver {
            viewInteract.displaySnack(msg = getString(R.string.updated_success),
                aMsg = getString(R.string.share_item),
                action = {
                    viewInteract.actionShareText(it)
                })
        })

        /*==============*/
        productList.observe(viewLifecycleOwner) {
            binding?.itemStockProduct?.edAuto?.bindStockProduct(list = it, action = { title ->
                handleEvent(
                    StockDetailsViewEvent.OnStockAutoCompleteSelect(
                        productCompany = title
                    )
                )
            })
        }
        addProductNavigateAttempt.observe(viewLifecycleOwner, EventObserver {
            viewInteract.displaySnack(
                msg = getString(R.string.empty_product_list),
                aMsg = getString(R.string.add),
                action = {
                    if (findNavController().currentDestination?.id == R.id.stockDetailsView) findNavController().navigate(
                        StockDetailsViewDirections.navigateToProduct()
                    )
                }
            )
        })
    }
}