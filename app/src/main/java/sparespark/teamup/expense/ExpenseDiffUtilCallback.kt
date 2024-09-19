package sparespark.teamup.expense

import androidx.recyclerview.widget.DiffUtil
import sparespark.teamup.data.model.expense.Expense

class ExpenseDiffUtilCallback : DiffUtil.ItemCallback<Expense>() {
    override fun areItemsTheSame(oldItem: Expense, newItem: Expense): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Expense, newItem: Expense): Boolean {
        return oldItem.id == newItem.id
    }
}
