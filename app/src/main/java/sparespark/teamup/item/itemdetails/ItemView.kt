package sparespark.teamup.item.itemdetails

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputLayout
import sparespark.teamup.R
import sparespark.teamup.core.MAX_ASSET_DIG
import sparespark.teamup.core.MAX_TOTAL_PRICE_DIG
import sparespark.teamup.core.bindClients
import sparespark.teamup.core.binding.ViewBindingHolder
import sparespark.teamup.core.binding.ViewBindingHolderImpl
import sparespark.teamup.core.enable
import sparespark.teamup.core.isNewIdItem
import sparespark.teamup.core.isValidNumFormated
import sparespark.teamup.core.relaunchCurrentView
import sparespark.teamup.core.setIconAction
import sparespark.teamup.core.setNumberDecimalInput
import sparespark.teamup.core.toEditable
import sparespark.teamup.core.wrapper.EventObserver
import sparespark.teamup.data.model.item.Item
import sparespark.teamup.databinding.ItemdetailsViewBinding
import sparespark.teamup.home.HomeViewInteract
import sparespark.teamup.home.base.BaseViewBehavioral
import sparespark.teamup.item.itemdetails.buildlogic.ItemViewInjector

class ItemView : Fragment(), View.OnClickListener, BaseViewBehavioral,
    ViewBindingHolder<ItemdetailsViewBinding> by ViewBindingHolderImpl() {

    private lateinit var viewModel: ItemViewModel
    private lateinit var viewInteract: HomeViewInteract

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = initBinding(ItemdetailsViewBinding.inflate(layoutInflater), this@ItemView) {
        setupViewInteract()
        setupViewInputs()
        setupViewModel()
        setupClickListener()
        viewModel.setupItemStatesObserver()
    }

    override fun setupViewInteract() {
        viewInteract = activity as HomeViewInteract
    }

    override fun setupViewInputs() {
        binding?.apply {
            itemNote.textInputLayout.hint = getString(R.string.note)
            itemClient.textInputLayout.hint = getString(R.string.search_client)
            inputPrice.apply {
                textInputLayout.hint = getString(R.string.price) + getString(R.string.required)
                edText.setNumberDecimalInput()
                edText.beginInputLayoutAssetWatcher(
                    inputLayout = textInputLayout,
                    maxDig = MAX_ASSET_DIG,
                    invalidMsg = getString(R.string.invalid)
                )
            }
            inputQuantity.apply {
                textInputLayout.hint = getString(R.string.quantity) + getString(R.string.required)
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
                    maxDig = MAX_TOTAL_PRICE_DIG,
                    invalidMsg = getString(R.string.invalid)
                )
            }
        }
    }

    override fun setupViewModel() {
        viewModel = ViewModelProvider(
            owner = this@ItemView,
            factory = ItemViewInjector(requireActivity().application).provideViewModelFactory()
        )[ItemViewModel::class.java]
        viewModel.handleEvent(ItemViewEvent.OnStartGetItem)
    }

    override fun setupClickListener() {
        binding?.apply {
            btnUpdateItem.setOnClickListener(this@ItemView)
            inputTotal.textInputLayout.setIconAction(
                icon = R.drawable.ic_refresh
            ) {
                viewModel.handleEvent(
                    ItemViewEvent.OnTotalPriceTxtClick(
                        price = inputPrice(), quantity = inputQuantity()
                    )
                )
            }
            inputQuantity.textInputLayout.setIconAction(
                icon = R.drawable.ic_refresh
            ) {
                viewModel.handleEvent(
                    ItemViewEvent.OnQuantityTxtClick(
                        price = inputPrice(), total = inputTotal()
                    )
                )
            }
            adminSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
                if (buttonView?.isPressed == true) viewModel.handleEvent(
                    ItemViewEvent.OnAdminSwitchCheck(
                        admin = isChecked
                    )
                )
            }
            sellSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
                if (buttonView?.isPressed == true) viewModel.handleEvent(
                    ItemViewEvent.OnSellSwitchCheck(
                        sell = isChecked
                    )
                )
            }
        }
    }

    private fun EditText.beginInputLayoutAssetWatcher(
        inputLayout: TextInputLayout,
        maxDig: Int,
        invalidMsg: String
    ) {
        this.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(s: Editable) {
            }

            override fun onTextChanged(
                text: CharSequence,
                start: Int,
                count: Int,
                after: Int
            ) {
                if (text.isNotEmpty()) if (text.toString().isValidNumFormated(maxDig)) {
                    inputLayout.isErrorEnabled = false
                    inputLayout.error = null
                } else inputLayout.error = invalidMsg
            }
        })
    }

    private fun ItemViewModel.setupItemStatesObserver() {
        loading.observe(viewLifecycleOwner) {
            viewInteract.updateProgressLoad(loading = it)
        }
        error.observe(viewLifecycleOwner) {
            viewInteract.displayToast(it.asString(context))
        }
        updated.observe(viewLifecycleOwner) {
            viewInteract.displayToast(context?.getString(R.string.updated_success))
            relaunchCurrentView()
        }
        adminSwitchState.observe(viewLifecycleOwner) {
            binding?.adminSwitch?.isChecked = it
        }
        sellSwitchState.observe(viewLifecycleOwner) {
            binding?.sellSwitch?.isChecked = it
        }
        clientEditableState.observe(viewLifecycleOwner) {
            binding?.itemClient?.textInputLayout?.enable(it)
        }
        reqTotalPriceTextState.observe(viewLifecycleOwner) {
            binding?.inputTotal?.edText?.text = it.toEditable()
        }
        reqQuantityTextState.observe(viewLifecycleOwner) {
            binding?.inputQuantity?.edText?.text = it.toEditable()
        }
        priceTextValidateState.observe(viewLifecycleOwner) {
            binding?.inputPrice?.textInputLayout?.error = getString(R.string.invalid)
        }
        quantityTextValidateState.observe(viewLifecycleOwner) {
            binding?.inputQuantity?.textInputLayout?.error = getString(R.string.invalid)
        }
        item.observe(viewLifecycleOwner) {
            if (!it.id.isNewIdItem()) setItem(it)
        }
        clientList.observe(viewLifecycleOwner) {
            it?.let { list ->
                binding?.itemClient?.edAuto?.bindClients(list = list, action = { name ->
                    handleEvent(ItemViewEvent.OnClientAutoCompleteSelect(name))
                })
            }
        }
        shareItemAttempt.observe(viewLifecycleOwner, EventObserver {
            viewInteract.displaySnack(msg = getString(R.string.updated_success),
                aMsg = getString(R.string.share_c),
                action = {
                    viewInteract.actionShareText(it)
                })
        })
        addNewClientAttempt.observe(viewLifecycleOwner, EventObserver {
            viewInteract.displaySnack(msg = getString(R.string.empty_client_list),
                aMsg = getString(R.string.add_c),
                action = {
                    if (findNavController().currentDestination?.id == R.id.itemView) findNavController().navigate(
                        ItemViewDirections.navigateToClient()
                    )
                })
        })
    }

    private fun setItem(item: Item) {
        binding?.apply {
            itemNote.edText.text = item.note.toEditable()
            itemClient.edAuto.text = item.clientEntry.name?.toEditable()
            inputPrice.edText.text = item.assetEntry.assetPrice.toString().toEditable()
            inputQuantity.edText.text = item.assetEntry.quantity.toString().toEditable()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        destroyBinding()
    }

    override fun onClick(view: View?) {
        if (view?.id == R.id.btn_update_item) viewModel.handleEvent(
            ItemViewEvent.OnUpdateBtnClick(
                price = inputPrice(), quantity = inputQuantity(), note = inputNote()
            )
        )
    }

    private fun inputPrice() = binding?.inputPrice?.edText?.text.toString()
    private fun inputQuantity() = binding?.inputQuantity?.edText?.text.toString()
    private fun inputTotal() = binding?.inputTotal?.edText?.text.toString()
    private fun inputNote() = binding?.itemNote?.edText?.text?.trim().toString()

}