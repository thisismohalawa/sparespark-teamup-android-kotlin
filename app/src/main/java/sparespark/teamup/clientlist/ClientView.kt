package sparespark.teamup.clientlist

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
import sparespark.teamup.clientlist.buildlogic.ClientViewInjector
import sparespark.teamup.core.base.BaseExpandedListView
import sparespark.teamup.core.bindCityList
import sparespark.teamup.core.binding.ViewBindingHolder
import sparespark.teamup.core.binding.ViewBindingHolderImpl
import sparespark.teamup.core.isMatch
import sparespark.teamup.core.relaunchCurrentView
import sparespark.teamup.core.selectQuery
import sparespark.teamup.core.setPhoneNumInput
import sparespark.teamup.core.setupListItemDecoration
import sparespark.teamup.core.toEditable
import sparespark.teamup.core.wrapper.EventObserver
import sparespark.teamup.data.model.client.Client
import sparespark.teamup.databinding.ExpandedListViewBinding
import sparespark.teamup.home.HomeActivityInteract

class ClientView : BaseExpandedListView(), View.OnClickListener,
    ViewBindingHolder<ExpandedListViewBinding> by ViewBindingHolderImpl() {

    private lateinit var clientAdapter: ClientAdapter
    private lateinit var viewModel: ClientViewModel
    private lateinit var viewInteract: HomeActivityInteract
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = initBinding(ExpandedListViewBinding.inflate(layoutInflater), this@ClientView) {
        setupBottomSheet()
        setupViewInteract()
        setupViewInputs()
        setupListAdapter()
        setupViewModel()
        viewModel.setupStatesObserver()
        setupClickListener()
    }

    private fun setupBottomSheet() {
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
            edTitle.hint = getString(R.string.client)
            itemSearch.mtSearchView.queryHint = getString(R.string.search_clients)
            itemShareClient.apply {
                edPhone.setPhoneNumInput()
            }
        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            owner = this@ClientView,
            factory = ClientViewInjector(requireActivity().application).provideViewModelFactory()
        )[ClientViewModel::class.java]
        viewModel.handleEvent(ClientEvent.OnStartGetClient)
        viewModel.handleEvent(ClientEvent.GetClientList)
    }

    private fun setupClickListener() {
        binding?.txtUpdate?.setOnClickListener(this@ClientView)
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            if (bottomSheetBehavior.state != BottomSheetBehavior.STATE_HIDDEN) {
                viewModel.handleEvent(
                    ClientEvent.HideBottomSheet
                )
            } else {
                findNavController().popBackStack()
            }
        }
    }

    private fun setupListAdapter() {
        clientAdapter = ClientAdapter()
        binding?.recItemList?.apply {
            adapter = clientAdapter
            setupListItemDecoration(context)
        }
        clientAdapter.event.observe(
            viewLifecycleOwner
        ) {
            viewModel.handleEvent(it)
        }
    }

    private fun setupSearchViewListener(list: List<Client>) {
        binding?.itemSearch?.mtSearchView?.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    val filteredList = mutableListOf<Client>()

                    for (client: Client in list)
                        if (client.name.isMatch(it)) filteredList.add(client)
                        else if (client.phone.isMatch(it)) filteredList.add(client)
                        else if (client.locationEntry.cityName?.isMatch(it) == true)
                            filteredList.add(client)
                    if (filteredList.isNotEmpty()) clientAdapter.submitList(filteredList)

                }
                return true
            }
        })
    }

    private fun bindClient(client: Client) {
        binding?.apply {
            edTitle.text = client.name.toEditable()
            itemShareClient.apply {
                edPhone.text = client.phone.toEditable()
                dataSpinner.selectQuery(query = client.locationEntry.cityName)
            }
        }
    }

    private fun ClientViewModel.setupStatesObserver() {
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
        client.observe(viewLifecycleOwner) {
            bindClient(it)
        }
        clientList.observe(viewLifecycleOwner) {
            clientAdapter.submitList(it)
            setupSearchViewListener(it)
        }
        dialClientAttempt.observe(viewLifecycleOwner, EventObserver {
            viewInteract.actionDial(it)
        })
        /*===================*/
        cityList.observe(viewLifecycleOwner) {
            binding?.dataSpinner?.bindCityList(
                list = it,
                selectTitle = null,
                selectAction = { position ->
                    handleEvent(ClientEvent.OnSpinnerCitySelect(position ?: 99))
                })
        }
        addCityNavigateAttempt.observe(viewLifecycleOwner, EventObserver {
            viewInteract.displaySnack(
                msg = getString(R.string.empty_city_list),
                aMsg = getString(R.string.add),
                action = {
                    if (findNavController().currentDestination?.id == R.id.clientView) findNavController().navigate(
                        ClientViewDirections.navigateToCity()
                    )
                }
            )
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        destroyBinding()
    }

    override fun onClick(p0: View?) {
        if (p0?.id == R.id.txt_update) viewModel.handleEvent(
            ClientEvent.OnUpdateTxtClick(
                name = binding?.edTitle?.text?.trim().toString(),
                phone = binding?.itemShareClient?.edPhone?.text?.trim().toString()
            )
        )
    }

}