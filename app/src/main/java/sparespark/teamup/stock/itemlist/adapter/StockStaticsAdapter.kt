package sparespark.teamup.stock.itemlist.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import sparespark.teamup.core.internal.toFormatedString
import sparespark.teamup.data.model.statics.StockStatics
import sparespark.teamup.databinding.ItemStaticsBinding
import sparespark.teamup.stock.itemlist.adapter.diffutil.StockStaticsDiffUtilCallback

class StockStaticsAdapter : ListAdapter<StockStatics, StockStaticsAdapter.ItemStaticsViewHolder>(
    StockStaticsDiffUtilCallback()
) {

    inner class ItemStaticsViewHolder(var binding: ItemStaticsBinding) :
        RecyclerView.ViewHolder(
            binding.root
        )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemStaticsViewHolder =
        ItemStaticsViewHolder(
            ItemStaticsBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: ItemStaticsViewHolder, position: Int) {
        val item = getItem(position)
        with(holder.binding) {
            itemHeader.txtTitle.text = item.quantity.toFormatedString()
            txtDes.text = "${item.product}\\${item.company}"
        }
    }

}