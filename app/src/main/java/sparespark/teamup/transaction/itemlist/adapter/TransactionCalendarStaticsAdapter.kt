package sparespark.teamup.transaction.itemlist.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import sparespark.teamup.data.model.statics.TransactionCalendarStatics
import sparespark.teamup.databinding.ItemStaticsBinding
import sparespark.teamup.transaction.itemlist.adapter.diffutil.TransactionCalendarStaticsDiffUtilCallback

class TransactionCalendarStaticsAdapter :
    ListAdapter<TransactionCalendarStatics, TransactionCalendarStaticsAdapter.ItemStaticsViewHolder>(
        TransactionCalendarStaticsDiffUtilCallback()
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
            itemHeader.txtTitle.text = item.totalCount.toString()
            txtDes.text = holder.itemView.context.getString(item.titleRes)
        }
    }
}