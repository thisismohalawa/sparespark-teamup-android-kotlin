package sparespark.teamup.transaction.itemlist.adapter.diffutil

import androidx.recyclerview.widget.DiffUtil
import sparespark.teamup.data.model.transaction.Transaction

class TransactionDiffUtilCallback : DiffUtil.ItemCallback<Transaction>() {
    override fun areItemsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
        return oldItem.id == newItem.id
    }
}
