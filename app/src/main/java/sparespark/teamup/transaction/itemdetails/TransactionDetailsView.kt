package sparespark.teamup.transaction.itemdetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import sparespark.teamup.R
import sparespark.teamup.core.MAX_ASSET_DIG
import sparespark.teamup.core.MAX_ASSET_PRICE_DIG
import sparespark.teamup.core.MAX_ASSET_TOTAL_PRICE_DIG
import sparespark.teamup.core.bindClients
import sparespark.teamup.core.binding.ViewBindingHolder
import sparespark.teamup.core.binding.ViewBindingHolderImpl
import sparespark.teamup.core.internal.beginInputLayoutAssetWatcher
import sparespark.teamup.core.isNewStringItem
import sparespark.teamup.core.setIconAction
import sparespark.teamup.core.setNumberDecimalInput
import sparespark.teamup.core.toEditable
import sparespark.teamup.core.wrapper.EventObserver
import sparespark.teamup.data.model.transaction.Transaction
import sparespark.teamup.databinding.TransactiondetailsViewBinding
import sparespark.teamup.home.HomeActivityInteract
import sparespark.teamup.transaction.itemdetails.buildlogic.TransactionViewInjector

class TransactionDetailsView : Fragment(), View.OnClickListener,
    ViewBindingHolder<TransactiondetailsViewBinding> by ViewBindingHolderImpl() {

    private lateinit var viewModel: TransactionDetailsViewModel
    private lateinit var viewInteract: HomeActivityInteract

    private fun inputPrice() = binding?.inputPrice?.edText?.text.toString()
    private fun inputQuantity() = binding?.inputQuantity?.edText?.text.toString()
    private fun inputTotal() = binding?.inputTotal?.edText?.text.toString()
    private fun inputNote() = binding?.itemNote?.edText?.text?.trim().toString()

    override fun onDestroyView() {
        super.onDestroyView()
        destroyBinding()
    }

    override fun onClick(view: View?) {
        if (view?.id == R.id.btn_update_item)
            viewModel.handleEvent(
                ItemDetailsViewEvent.OnUpdateBtnClick(
                    price = inputPrice(),
                    quantity = inputQuantity(),
                    note = inputNote()
                )
            )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View =
        initBinding(
            TransactiondetailsViewBinding.inflate(layoutInflater),
            this@TransactionDetailsView
        ) {
            setupViewInteract()
            setupViewInputs()
            setupViewModel()
            viewModel.setupItemStatesObserver()
            setupClickListener()
        }

    private fun setupViewInteract() {
        viewInteract = activity as HomeActivityInteract
    }

    private fun setupViewInputs() {
        binding?.apply {
            itemNote.textInputLayout.hint = getString(R.string.note)
            itemClient.textInputLayout.hint = getString(R.string.client)
            inputPrice.apply {
                textInputLayout.hint = getString(R.string.price)
                edText.setNumberDecimalInput()
                edText.beginInputLayoutAssetWatcher(
                    inputLayout = textInputLayout,
                    maxDig = MAX_ASSET_PRICE_DIG,
                    invalidMsg = getString(R.string.invalid)
                )
            }
            inputQuantity.apply {
                textInputLayout.hint = getString(R.string.quantity)
                edText.setNumberDecimalInput()
                edText.beginInputLayoutAssetWatcher(
                    inputLayout = textInputLayout,
                    maxDig = MAX_ASSET_DIG,
                    invalidMsg = getString(R.string.invalid)
                )
            }
            inputTotal.apply {
                textInputLayout.hint = getString(R.string.total_price)
                edText.setNumberDecimalInput()
                edText.beginInputLayoutAssetWatcher(
                    inputLayout = textInputLayout,
                    maxDig = MAX_ASSET_TOTAL_PRICE_DIG,
                    invalidMsg = getString(R.string.invalid)
                )
            }
        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            owner = this@TransactionDetailsView,
            factory = TransactionViewInjector(requireActivity().application).provideViewModelFactory()
        )[TransactionDetailsViewModel::class.java]
        viewModel.handleEvent(ItemDetailsViewEvent.OnStartGetItem)
    }

    private fun setupClickListener() {
        binding?.apply {
            btnUpdateItem.setOnClickListener(this@TransactionDetailsView)
            tempSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
                if (buttonView?.isPressed == true) viewModel.handleEvent(
                    ItemDetailsViewEvent.OnTempSwitchCheck(
                        temp = isChecked
                    )
                )
            }
            sellSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
                if (buttonView?.isPressed == true) viewModel.handleEvent(
                    ItemDetailsViewEvent.OnSellSwitchCheck(
                        sell = isChecked
                    )
                )
            }
            inputTotal.textInputLayout.setIconAction(icon = R.drawable.ic_refresh) {
                viewModel.handleEvent(
                    ItemDetailsViewEvent.OnTotalPriceTxtClick(
                        price = inputPrice(), quantity = inputQuantity()
                    )
                )
            }
            inputQuantity.textInputLayout.setIconAction(icon = R.drawable.ic_refresh) {
                viewModel.handleEvent(
                    ItemDetailsViewEvent.OnQuantityTxtClick(
                        price = inputPrice(), total = inputTotal()
                    )
                )
            }
        }
    }

    private fun bindTransaction(transaction: Transaction) {
        binding?.apply {
            itemNote.edText.text = transaction.note.toEditable()
            itemClient.edAuto.text = transaction.clientEntry.name?.toEditable()
            inputPrice.edText.text = transaction.assetEntry.assetPrice.toString().toEditable()
            inputQuantity.edText.text = transaction.assetEntry.quantity.toString().toEditable()
        }
    }

    private fun TransactionDetailsViewModel.setupItemStatesObserver() {
        loading.observe(viewLifecycleOwner) {
            viewInteract.updateProgressLoad(loading = it)
        }
        error.observe(viewLifecycleOwner) {
            viewInteract.displayToast(it.asString(context))
        }
        updated.observe(viewLifecycleOwner) {
            viewInteract.displayToast(context?.getString(R.string.updated_success))
        }
        item.observe(viewLifecycleOwner) {
            if (!it.id.isNewStringItem()) bindTransaction(it)
        }
        /*================*/
        tempSwitchState.observe(viewLifecycleOwner) {
            binding?.tempSwitch?.isChecked = it
        }
        sellSwitchState.observe(viewLifecycleOwner) {
            binding?.sellSwitch?.isChecked = it
        }
        /*================*/
        reqTotalPriceTextState.observe(viewLifecycleOwner) {
            binding?.inputTotal?.edText?.text = it.toEditable()
        }
        reqQuantityTextState.observe(viewLifecycleOwner) {
            binding?.inputQuantity?.edText?.text = it.toEditable()
        }
        /*================*/
        priceTextValidateState.observe(viewLifecycleOwner) {
            binding?.inputPrice?.textInputLayout?.error = getString(R.string.invalid)
        }
        quantityTextValidateState.observe(viewLifecycleOwner) {
            binding?.inputQuantity?.textInputLayout?.error = getString(R.string.invalid)
        }
        clientQueryValidateState.observe(viewLifecycleOwner) {
            binding?.itemClient?.textInputLayout?.apply {
                if (it) {
                    isErrorEnabled = false
                    error = null
                } else error = getString(R.string.invalid)
            }
        }

        /*================*/
        shareItemAttempt.observe(viewLifecycleOwner, EventObserver {
            viewInteract.displaySnack(msg = getString(R.string.updated_success),
                aMsg = getString(R.string.share_item),
                action = {
                    viewInteract.actionShareText(it)
                })
        })
        /*================*/
        clientList.observe(viewLifecycleOwner) {
            it?.let { list ->
                binding?.itemClient?.edAuto?.bindClients(list = list, action = { name ->
                    handleEvent(ItemDetailsViewEvent.OnClientAutoCompleteSelect(name))
                })
            }
        }
        addClientNavigateAttempt.observe(viewLifecycleOwner, EventObserver {
            viewInteract.displaySnack(
                msg = getString(R.string.empty_client_list),
                aMsg = getString(R.string.add),
                action = {
                    if (findNavController().currentDestination?.id == R.id.transactionDetailsView) findNavController().navigate(
                        TransactionDetailsViewDirections.navigateToClient()
                    )
                }
            )
        })
    }
}