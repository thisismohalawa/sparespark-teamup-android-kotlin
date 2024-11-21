package sparespark.teamup.transaction.itemlist.adapter.diffutil

import androidx.recyclerview.widget.DiffUtil
import sparespark.teamup.data.model.statics.TransactionCalendarStatics

class TransactionCalendarStaticsDiffUtilCallback : DiffUtil.ItemCallback<TransactionCalendarStatics>() {
    override fun areItemsTheSame(oldItem: TransactionCalendarStatics, newItem: TransactionCalendarStatics): Boolean {
        return oldItem.titleRes == newItem.titleRes
    }

    override fun areContentsTheSame(oldItem: TransactionCalendarStatics, newItem: TransactionCalendarStatics): Boolean {
        return oldItem.titleRes == newItem.titleRes
    }
}