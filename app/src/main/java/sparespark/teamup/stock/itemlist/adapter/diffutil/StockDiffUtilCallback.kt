package sparespark.teamup.stock.itemlist.adapter.diffutil

import androidx.recyclerview.widget.DiffUtil
import sparespark.teamup.data.model.stock.Stock

class StockDiffUtilCallback : DiffUtil.ItemCallback<Stock>() {
    override fun areItemsTheSame(oldItem: Stock, newItem: Stock): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Stock, newItem: Stock): Boolean {
        return oldItem.id == newItem.id
    }
}