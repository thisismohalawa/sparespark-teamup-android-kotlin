package sparespark.teamup.note

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import sparespark.teamup.R
import sparespark.teamup.core.MAX_NOTE_DIG
import sparespark.teamup.core.MIN_TITLE_DIG
import sparespark.teamup.core.beginLayoutTitleLengthWatcher
import sparespark.teamup.core.binding.ViewBindingHolder
import sparespark.teamup.core.binding.ViewBindingHolderImpl
import sparespark.teamup.core.isNewIdItem
import sparespark.teamup.core.toEditable
import sparespark.teamup.core.visible
import sparespark.teamup.core.wrapper.EventObserver
import sparespark.teamup.databinding.InputsViewBinding
import sparespark.teamup.home.HomeViewInteract
import sparespark.teamup.home.base.BaseViewBehavioral
import sparespark.teamup.note.buildlogic.NoteInjector

class NoteView : Fragment(),
    BaseViewBehavioral,
    View.OnClickListener,
    ViewBindingHolder<InputsViewBinding> by ViewBindingHolderImpl() {

    private lateinit var viewModel: NoteViewModel
    private lateinit var viewInteract: HomeViewInteract


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = initBinding(InputsViewBinding.inflate(layoutInflater), this@NoteView) {
        setupViewInteract()
        setupViewInputs()
        setupViewModel()
        viewModel.startObserving()
        setupClickListener()
    }

    override fun setupViewInteract() {
        viewInteract = activity as HomeViewInteract
    }

    override fun setupViewInputs() {
        binding?.apply {
            btnAction.text = getString(R.string.update)
            switchAction.text = getString(R.string.admins_only)
            switchAction.visible(true)
            itemInput2.textInputLayout.visible(false)
            itemInput1.apply {
                textInputLayout.hint = getString(R.string.note) + getString(R.string.required)
                edText.beginLayoutTitleLengthWatcher(
                    inputLayout = itemInput1.textInputLayout,
                    minDig = MIN_TITLE_DIG,
                    maxDig = MAX_NOTE_DIG,
                    eMsg = getString(R.string.invalid)
                )
            }
        }
    }

    override fun setupViewModel() {
        viewModel = ViewModelProvider(
            owner = this@NoteView,
            factory = NoteInjector(requireActivity().application).provideViewModelFactory()
        )[NoteViewModel::class.java]
        viewModel.handleEvent(NoteEvent.OnStartGetNote)
    }

    override fun setupClickListener() {
        binding?.apply {
            btnAction.setOnClickListener(this@NoteView)
            switchAction.setOnCheckedChangeListener { buttonView, isChecked ->
                if (buttonView?.isPressed == true) viewModel.handleEvent(
                    NoteEvent.OnAdminSwitchCheck(
                        admin = isChecked
                    )
                )
            }
        }
    }

    private fun NoteViewModel.startObserving() {
        loading.observe(viewLifecycleOwner) {
            binding?.progressBar?.visible(it)
        }
        error.observe(viewLifecycleOwner) {
            viewInteract.displayToast(it.asString(context))
        }
        updated.observe(viewLifecycleOwner) {
            if (findNavController().currentDestination?.id == R.id.noteView) findNavController().navigate(
                NoteViewDirections.navigateToItemList()
            )
        }
        adminSwitchState.observe(viewLifecycleOwner) {
            binding?.switchAction?.isChecked = it
        }
        noteTxtValidateState.observe(viewLifecycleOwner) {
            binding?.itemInput1?.textInputLayout?.error = getString(R.string.invalid)
        }
        note.observe(viewLifecycleOwner) {
            if (!it.id.isNewIdItem()) binding?.itemInput1?.edText?.text = it.title.toEditable()
        }
        shareNoteAttempt.observe(viewLifecycleOwner, EventObserver {
            viewInteract.displaySnack(
                msg = getString(R.string.updated_success),
                aMsg = getString(R.string.share_c),
                action = {
                    viewInteract.actionShareText(it)
                }
            )
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        destroyBinding()
    }

    override fun onClick(p0: View?) {
        if (p0?.id == R.id.btn_action) viewModel.handleEvent(
            NoteEvent.OnUpdateTxtClick(
                title = binding?.itemInput1?.edText?.text?.trim().toString()
            )
        )
    }
}