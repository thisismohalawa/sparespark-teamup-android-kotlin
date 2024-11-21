package sparespark.teamup.transaction.itemlist.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import sparespark.teamup.R
import sparespark.teamup.core.bindDataWithHint
import sparespark.teamup.core.bindHistory
import sparespark.teamup.core.displayConfirmDialog
import sparespark.teamup.core.internal.getTotal
import sparespark.teamup.core.internal.plusCurrency
import sparespark.teamup.core.internal.toFormatedString
import sparespark.teamup.core.onViewedClickUpdateExpanding
import sparespark.teamup.core.setCustomColor
import sparespark.teamup.core.setCustomIcon
import sparespark.teamup.core.setLabeled
import sparespark.teamup.core.setRedTitle
import sparespark.teamup.core.visible
import sparespark.teamup.data.model.AssetEntry
import sparespark.teamup.data.model.transaction.Transaction
import sparespark.teamup.databinding.ItemTransactionBinding
import sparespark.teamup.transaction.BaseTransactionListEvent
import sparespark.teamup.transaction.itemlist.adapter.diffutil.TransactionDiffUtilCallback

class TransactionAdapter(
    private val isExpanded: Boolean = false,
    val event: MutableLiveData<BaseTransactionListEvent> = MutableLiveData()
) : ListAdapter<Transaction, TransactionAdapter.TransactionViewHolder>(TransactionDiffUtilCallback()) {

    inner class TransactionViewHolder(var binding: ItemTransactionBinding) :
        RecyclerView.ViewHolder(
            binding.root
        )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder =
        TransactionViewHolder(
            ItemTransactionBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val item = getItem(position)
        val iContext = holder.itemView.context
        val adapterPosition = holder.adapterPosition

        with(holder.binding) {

            expandableLayout.checkIfExpandedOrNoted(
                note = item.note
            )

            txtTotal.text = item.assetEntry.toTotalPrice(iContext)

            txtDes.text = bindDataWithHint(
                mHint = iContext.getString(R.string.date),
                mData = item.creationDate,
                sHint = iContext.getString(R.string.price),
                sData = item.toAssetClientTitle(iContext)
            )
            txtHistory.bindHistory(
                updateBy = item.updateBy,
                updateDate = item.updateDate,
                context = iContext
            )
            txtNote.text = bindDataWithHint(
                hint = iContext.getString(R.string.note), data = item.note
            )
            txtTotal.setCustomIcon(
                isIncome = item.sell,
                inDrawable = R.drawable.ic_arrow_sell,
                outDrawable = R.drawable.ic_arrow_buy
            )
            item.active.let {
                activeCheckbox.isChecked = !it
                txtTotal.setLabeled(
                    labeled = !it
                )
            }
            if (item.temp) {
                txtDes.setCustomColor(R.color.red, iContext)
                txtHistory.setCustomColor(R.color.red, iContext)
                txtTotal.setCustomColor(R.color.red, iContext)
                txtHistory.setCustomColor(R.color.red, iContext)
            }
            txtTotal.setOnClickListener {
                it.inflateTitleMenu(adapterPosition, isTemp = item.temp)
            }
            imgMenu.setOnClickListener {
                it.inflateItemMenu(adapterPosition)
            }
            root.setOnLongClickListener {
                event.value = BaseTransactionListEvent.OnItemListLongClick(holder.adapterPosition)
                true
            }
            root.setOnClickListener {
                rootLayout.onViewedClickUpdateExpanding(
                    expandingLayout = expandableLayout
                )
            }
            activeCheckbox.setOnCheckedChangeListener { buttonView, isChecked ->
                if (buttonView?.isPressed == true) event.value =
                    BaseTransactionListEvent.OnItemListCheckboxClick(
                        pos = adapterPosition, isActive = !isChecked
                    )
            }
        }
    }

    private fun AssetEntry.toTotalPrice(
        context: Context
    ): String = getTotal(assetPrice, quantity).toFormatedString().plusCurrency(context)

    private fun ViewGroup.checkIfExpandedOrNoted(
        note: String?
    ) {
        if (isExpanded) visible(true)
        if (note?.isNotBlank() == true) visible(true)
    }

    private fun Transaction.toAssetClientTitle(context: Context): String =
        "${assetEntry.assetPrice}, " + "${context.getString(R.string.quantity)}: ${assetEntry.quantity}" + if (clientEntry.name?.isNotBlank() == true) "\n${
            context.getString(R.string.client)
        }: ${clientEntry.name}, ${clientEntry.city}" else ""

    private fun View.inflateItemMenu(position: Int) {
        val popupMenu = PopupMenu(this@inflateItemMenu.context, this)
        popupMenu.apply {
            inflate(R.menu.transaction_list_menu)
            menu.findItem(R.id.delete_menu).setRedTitle()
            setOnMenuItemClickListener { item ->
                when (item?.itemId) {
                    R.id.update_menu -> event.value = BaseTransactionListEvent.OnMenuItemUpdateClick(position)

                    R.id.refresh_menu -> event.value =
                        BaseTransactionListEvent.OnMenuItemListRefresh

                    R.id.export_menu -> context.displayConfirmDialog(
                        title = R.string.export_summary
                    ) {
                        event.value = BaseTransactionListEvent.OnMenuItemListExportClick
                    }

                    R.id.delete_menu -> this@inflateItemMenu.context.displayConfirmDialog(
                        title = R.string.delete_permanently
                    ) {
                        event.value = BaseTransactionListEvent.OnMenuItemListDeleteClick(
                            position
                        )
                    }
                }
                false
            }
            show()
        }
    }

    private fun View.inflateTitleMenu(position: Int, isTemp: Boolean) {
        val popupMenu = PopupMenu(this@inflateTitleMenu.context, this)
        popupMenu.apply {
            inflate(R.menu.transaction_title_menu)
            menu.findItem(R.id.push_menu).setRedTitle()
            setOnMenuItemClickListener { item ->
                when (item?.itemId) {
                    R.id.share_menu -> event.value =
                        BaseTransactionListEvent.OnMenuItemListShareClick(position)

                    R.id.copy_menu -> event.value =
                        BaseTransactionListEvent.OnMenuItemListCopyClick(
                            position
                        )

                    R.id.push_menu -> if (isTemp) this@inflateTitleMenu.context.displayConfirmDialog(
                        title = R.string.push_item
                    ) {
                        event.value =
                            BaseTransactionListEvent.OnMenuItemListPushClick(position)
                    }
                }
                false
            }
            show()
        }
    }

}