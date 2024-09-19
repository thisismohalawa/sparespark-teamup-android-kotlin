package sparespark.teamup.item.adapter

import android.content.Context
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import sparespark.teamup.R
import sparespark.teamup.core.bindDateWithInfo
import sparespark.teamup.core.bindNote
import sparespark.teamup.core.displayConfirmDialog
import sparespark.teamup.core.getTotal
import sparespark.teamup.core.plusCurrency
import sparespark.teamup.core.setCustomIcon
import sparespark.teamup.core.setLabeled
import sparespark.teamup.core.setRedTitle
import sparespark.teamup.core.toFormatedString
import sparespark.teamup.core.visible
import sparespark.teamup.data.model.item.AssetEntry
import sparespark.teamup.data.model.item.Item
import sparespark.teamup.databinding.ItemBetaBinding
import sparespark.teamup.item.BaseItemListEvent

class ItemAdapter(
    private val isExpanded: Boolean = false,
    val event: MutableLiveData<BaseItemListEvent> = MutableLiveData()
) : ListAdapter<Item, RecyclerView.ViewHolder>(ItemXDiffUtilCallback()) {

    inner class ItemXViewHolder(var binding: ItemBetaBinding) : RecyclerView.ViewHolder(
        binding.root
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ItemXViewHolder(
            ItemBetaBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val itemX = getItem(position)
        with(holder as ItemXViewHolder) {
            val iContext = itemView.context
            with(binding) {
                expandableLayout.checkIfExpandedOrNoted(itemX.note)
                txtTotal.setCustomIcon(
                    isIncome = itemX.sell,
                    inDrawable = R.drawable.ic_sell,
                    outDrawable = R.drawable.ic_buy
                )


                itemX.active.let {
                    activeCheckbox.isChecked = !it
                    txtTotal.setLabeled(
                        labeled = !it
                    )
                }

                txtTotal.text = itemX.assetEntry.toTotalPrice(iContext)

                txtDes.text = bindDateWithInfo(
                    dHint = iContext.getString(R.string.date),
                    iHint = iContext.getString(R.string.price),
                    date = itemX.creationDate,
                    info = itemX.toAssetClientTitle(iContext)
                )
                txtNote.text = bindNote(
                    nHint = iContext.getString(R.string.note),
                    note = itemX.note
                )
                txtHistory.setUpdateTitle(
                    updateDate = itemX.updateDate,
                    updateBy = itemX.updateBy,
                    context = iContext
                )
                activeCheckbox.setOnCheckedChangeListener { buttonView, isChecked ->
                    if (buttonView?.isPressed == true) event.value =
                        BaseItemListEvent.OnItemListCheckboxClick(
                            pos = adapterPosition, isActive = !isChecked
                        )
                }
                imgMenu.setOnClickListener { v ->
                    v.inflateItemMenu(adapterPosition)
                }
                txtTotal.setOnClickListener { v ->
                    v.inflateItemTitleMenu(adapterPosition)
                }
                txtDes.setOnClickListener { v ->
                    v.inflateItemTitleMenu(adapterPosition)
                }
                contentLayout.setOnLongClickListener {
                    event.value = BaseItemListEvent.OnItemListLongClick(adapterPosition)
                    true
                }
                contentLayout.setOnClickListener {
                    contentLayout.onViewedClickUpdateExpanding(
                        expandingLayout = expandableLayout
                    )
                    event.value = BaseItemListEvent.OnItemListSingleClick(adapterPosition)
                }
            }
        }
    }

    private fun ViewGroup.checkIfExpandedOrNoted(note: String?) {
        if (isExpanded) visible(true)
        if (note?.isNotBlank() == true) visible(true)
    }

    private fun AssetEntry.toTotalPrice(
        context: Context
    ): String = getTotal(assetPrice, quantity).toFormatedString().plusCurrency(context)

    private fun Item.toAssetClientTitle(context: Context): String =
        "${assetEntry.assetPrice}, " + "${context.getString(R.string.quantity)}: ${assetEntry.quantity}" + if (clientEntry.name?.isNotBlank() == true) "\n${
            context.getString(R.string.client)
        }: ${clientEntry.name}, ${clientEntry.city}" else ""

    private fun TextView.setUpdateTitle(
        updateBy: String, updateDate: String, context: Context
    ) {
        if (updateDate.isNotBlank()) {
            this.text =
                if (updateBy.isNotBlank()) "${context.getString(R.string.update_by)}: $updateBy" + "\n$updateDate" else updateDate
            this.visible(true)
        }
    }

    private fun View.inflateItemMenu(position: Int) {
        val popupMenu = PopupMenu(this@inflateItemMenu.context, this)
        popupMenu.apply {
            inflate(R.menu.item_menu)
            menu.findItem(R.id.delete_menu).setRedTitle()
            setOnMenuItemClickListener { item ->
                when (item?.itemId) {
                    R.id.delete_menu -> this@inflateItemMenu.context.displayConfirmDialog(
                        title = R.string.delete_permanently
                    ) {
                        event.value = BaseItemListEvent.OnMenuItemListDeleteClick(
                            position
                        )
                    }

                    R.id.refresh_menu -> event.value = BaseItemListEvent.OnMenuItemListRefresh
                    R.id.export_menu -> context.displayConfirmDialog(
                        title = R.string.export_summary
                    ) {
                        event.value = BaseItemListEvent.OnMenuItemListExportClick
                    }
                }
                false
            }
            show()
        }
    }

    private fun ViewGroup.onViewedClickUpdateExpanding(
        expandingLayout: ViewGroup
    ) {
        if (expandingLayout.visibility != View.VISIBLE) {
            TransitionManager.beginDelayedTransition(this, AutoTransition())
            expandingLayout.visible(true)
        }
    }

    private fun View.inflateItemTitleMenu(position: Int) {
        val popupMenu = PopupMenu(this@inflateItemTitleMenu.context, this)
        popupMenu.apply {
            inflate(R.menu.item_title_menu)
            setOnMenuItemClickListener { item ->
                when (item?.itemId) {
                    R.id.update_menu -> event.value = BaseItemListEvent.OnItemListClick(position)
                    R.id.share_menu -> event.value =
                        BaseItemListEvent.OnMenuItemListShareClick(position)

                    R.id.copy_menu -> event.value = BaseItemListEvent.OnMenuItemListCopyClick(
                        position
                    )
                }
                false
            }
            show()
        }
    }
}
