package sparespark.teamup.stock.itemlist.adapter.diffutil

import androidx.recyclerview.widget.DiffUtil
import sparespark.teamup.data.model.statics.StockStatics

class StockStaticsDiffUtilCallback : DiffUtil.ItemCallback<StockStatics>() {
    override fun areItemsTheSame(oldItem: StockStatics, newItem: StockStatics): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: StockStatics, newItem: StockStatics): Boolean {
        return oldItem.id == newItem.id
    }
}