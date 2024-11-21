package sparespark.teamup.stock.itemlist.adapter

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
import sparespark.teamup.core.internal.plusQuan
import sparespark.teamup.core.internal.toFormatedString
import sparespark.teamup.core.onViewedClickUpdateExpanding
import sparespark.teamup.core.setCustomColor
import sparespark.teamup.core.setCustomIcon
import sparespark.teamup.core.setRedTitle
import sparespark.teamup.core.visible
import sparespark.teamup.data.model.stock.Stock
import sparespark.teamup.databinding.ItemStockBinding
import sparespark.teamup.stock.BaseStockListEvent
import sparespark.teamup.stock.itemlist.adapter.diffutil.StockDiffUtilCallback

class StockListAdapter(
    private val isExpanded: Boolean = false,
    val event: MutableLiveData<BaseStockListEvent> = MutableLiveData()
) : ListAdapter<Stock, StockListAdapter.ItemStockViewHolder>(StockDiffUtilCallback()) {

    inner class ItemStockViewHolder(var binding: ItemStockBinding) : RecyclerView.ViewHolder(
        binding.root
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemStockViewHolder =
        ItemStockViewHolder(
            ItemStockBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )

    override fun onBindViewHolder(holder: ItemStockViewHolder, position: Int) {
        val item = getItem(position)
        val iContext = holder.itemView.context
        val adapterPosition = holder.adapterPosition
        with(holder.binding) {

            expandableLayout.checkIfExpandedOrNoted(
                note = item.note
            )
            txtQuantity.text = item.assetEntry.quantity?.toFormatedString()?.plusQuan(iContext)

            txtProduct.text = bindDataWithHint(
                mHint = iContext.getString(R.string.product),
                mData = item.productEntry.name ?: "",
                sHint = iContext.getString(R.string.company),
                sData = item.productEntry.company ?: ""
            )
            txtDes.text = bindDataWithHint(
                hint = iContext.getString(R.string.date), data = item.creationDate
            )
            txtHistory.bindHistory(
                updateBy = item.updateBy,
                updateDate = item.updateDate,
                context = iContext
            )
            txtNote.text = bindDataWithHint(
                hint = iContext.getString(R.string.note), data = item.note
            )
            txtQuantity.setCustomIcon(
                isIncome = item.sell,
                inDrawable = R.drawable.ic_arrow_sell,
                outDrawable = R.drawable.ic_arrow_buy
            )
            if (item.temp) {
                txtProduct.setCustomColor(R.color.red, iContext)
                txtDes.setCustomColor(R.color.red, iContext)
                txtHistory.setCustomColor(R.color.red, iContext)
            }
            txtQuantity.setOnClickListener {
                it.inflateTitleMenu(adapterPosition, isTemp = item.temp)
            }
            imgMenu.setOnClickListener {
                it.inflateItemMenu(adapterPosition)
            }
            root.setOnLongClickListener {
                event.value = BaseStockListEvent.OnListItemLongClick(holder.adapterPosition)
                true
            }
            root.setOnClickListener {
                rootLayout.onViewedClickUpdateExpanding(
                    expandingLayout = expandableLayout
                )
            }
        }
    }

    private fun ViewGroup.checkIfExpandedOrNoted(
        note: String?
    ) {
        if (isExpanded) visible(true)
        if (note?.isNotBlank() == true) visible(true)
    }

    private fun View.inflateItemMenu(position: Int) {
        val popupMenu = PopupMenu(this@inflateItemMenu.context, this)
        popupMenu.apply {
            inflate(R.menu.stock_list_menu)
            menu.findItem(R.id.delete_menu).setRedTitle()
            setOnMenuItemClickListener { item ->
                when (item?.itemId) {
                    R.id.update_menu -> event.value = BaseStockListEvent.OnMenuItemUpdateClick(
                        position
                    )

                    R.id.refresh_menu -> event.value = BaseStockListEvent.OnMenuItemRefresh

                    R.id.export_menu -> context.displayConfirmDialog(
                        title = R.string.export_summary
                    ) {
                        event.value = BaseStockListEvent.OnMenuItemExportClick
                    }

                    R.id.delete_menu -> this@inflateItemMenu.context.displayConfirmDialog(
                        title = R.string.delete_permanently
                    ) {
                        event.value = BaseStockListEvent.OnMenuItemDeleteClick(
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
            inflate(R.menu.stock_title_menu)
            menu.findItem(R.id.push_menu).setRedTitle()
            setOnMenuItemClickListener { item ->
                when (item?.itemId) {
                    R.id.share_menu -> event.value = BaseStockListEvent.OnMenuItemShareClick(
                        position
                    )

                    R.id.copy_menu -> event.value = BaseStockListEvent.OnMenuItemCopyClick(
                        position
                    )

                    R.id.push_menu -> if (isTemp) this@inflateTitleMenu.context.displayConfirmDialog(
                        title = R.string.push_item
                    ) {
                        event.value = BaseStockListEvent.OnMenuItemPushClick(position)
                    }
                }
                false
            }
            show()
        }
    }

}