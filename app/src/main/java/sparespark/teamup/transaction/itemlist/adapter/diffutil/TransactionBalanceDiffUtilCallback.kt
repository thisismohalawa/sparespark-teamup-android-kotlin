package sparespark.teamup.transaction.itemlist.adapter.diffutil

import androidx.recyclerview.widget.DiffUtil
import sparespark.teamup.data.model.balance.TransactionBalance

class TransactionBalanceDiffUtilCallback : DiffUtil.ItemCallback<TransactionBalance>() {
    override fun areItemsTheSame(
        oldItem: TransactionBalance,
        newItem: TransactionBalance
    ): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: TransactionBalance,
        newItem: TransactionBalance
    ): Boolean {
        return oldItem.id == newItem.id
    }
}