package sparespark.teamup.stock.itemsell

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import sparespark.teamup.R
import sparespark.teamup.core.MAX_ASSET_DIG
import sparespark.teamup.core.bindClients
import sparespark.teamup.core.bindStockList
import sparespark.teamup.core.binding.ViewBindingHolder
import sparespark.teamup.core.binding.ViewBindingHolderImpl
import sparespark.teamup.core.internal.beginInputLayoutAssetWatcher
import sparespark.teamup.core.setNumberDecimalInput
import sparespark.teamup.core.toEditable
import sparespark.teamup.core.wrapper.EventObserver
import sparespark.teamup.databinding.StocksellViewBinding
import sparespark.teamup.home.HomeActivityInteract
import sparespark.teamup.stock.itemsell.buildllogic.StockSellViewInjector

class StockSellView : Fragment(), View.OnClickListener,
    ViewBindingHolder<StocksellViewBinding> by ViewBindingHolderImpl() {

    private lateinit var viewModel: StockSellViewModel
    private lateinit var viewInteract: HomeActivityInteract

    private fun inputQuantity() = binding?.inputQuantity?.edText?.text.toString()
    private fun inputNote() = binding?.itemNote?.edText?.text?.trim().toString()

    override fun onDestroyView() {
        super.onDestroyView()
        destroyBinding()
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.btn_sell_item -> viewModel.handleEvent(
                StockSellViewEvent.OnUpdateBtnClick(
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
    ): View = initBinding(StocksellViewBinding.inflate(layoutInflater), this@StockSellView) {
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
            itemClient.textInputLayout.hint = getString(R.string.client)
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
            owner = this@StockSellView,
            factory = StockSellViewInjector(requireActivity().application).provideViewModelFactory()
        )[StockSellViewModel::class.java]
        viewModel.handleEvent(StockSellViewEvent.OnStartGetItem)
    }

    private fun setupClickListener() {
        binding?.apply {
            btnSellItem.setOnClickListener(this@StockSellView)
            tempSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
                if (buttonView?.isPressed == true) viewModel.handleEvent(
                    StockSellViewEvent.OnTempSwitchCheck(
                        temp = isChecked
                    )
                )
            }
        }
    }


    private fun StockSellViewModel.setupStatesObserver() {
        loading.observe(viewLifecycleOwner) {
            viewInteract.updateProgressLoad(it)
        }
        error.observe(viewLifecycleOwner) {
            viewInteract.displayToast(it.asString(context))
        }
        sell.observe(viewLifecycleOwner) {
            viewInteract.displayToast(context?.getString(R.string.updated_success))
        }

        /*==================*/
        quantityTextValidateState.observe(viewLifecycleOwner) {
            binding?.inputQuantity?.textInputLayout?.error = getString(R.string.invalid)
        }
        assetSellQuantity.observe(viewLifecycleOwner) {
            binding?.inputQuantity?.edText?.text = it.toString().toEditable()
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
        clientQueryValidateState.observe(viewLifecycleOwner) {
            binding?.itemClient?.textInputLayout?.apply {
                if (it) {
                    isErrorEnabled = false
                    error = null
                } else error = getString(R.string.invalid)
            }
        }
        addClientNavigateAttempt.observe(viewLifecycleOwner, EventObserver {
            viewInteract.displaySnack(
                msg = getString(R.string.empty_client_list),
                aMsg = getString(R.string.add),
                action = {
                    if (findNavController().currentDestination?.id == R.id.stockSellView) findNavController().navigate(
                        StockSellViewDirections.navigateToClient()
                    )
                }
            )
        })
        /*==============*/
        clientList.observe(viewLifecycleOwner) {
            binding?.itemClient?.edAuto?.bindClients(list = it, action = { name ->
                handleEvent(StockSellViewEvent.OnClientAutoCompleteSelect(name))
            })
        }
        stockList.observe(viewLifecycleOwner) {
            binding?.stockProductSpinner?.bindStockList(
                list = it,
                selectAction = { position ->
                    handleEvent(StockSellViewEvent.OnSpinnerStockProductSelect(position))
                })
        }
    }
}