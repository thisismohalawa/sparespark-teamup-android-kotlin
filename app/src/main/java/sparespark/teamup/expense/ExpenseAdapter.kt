package sparespark.teamup.expense

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import sparespark.teamup.R
import sparespark.teamup.core.bindDateWithInfo
import sparespark.teamup.core.bindNote
import sparespark.teamup.core.displayConfirmDialog
import sparespark.teamup.core.plusCurrency
import sparespark.teamup.core.setCustomIcon
import sparespark.teamup.core.setRedTitle
import sparespark.teamup.core.toFormatedString
import sparespark.teamup.data.model.expense.Expense
import sparespark.teamup.databinding.ItemExpenseBinding

class ExpenseAdapter(
    val event: MutableLiveData<ExpenseEvent> = MutableLiveData()
) : ListAdapter<Expense, RecyclerView.ViewHolder>(ExpenseDiffUtilCallback()) {

    inner class ExpenseViewHolder(var binding: ItemExpenseBinding) : RecyclerView.ViewHolder(
        binding.root
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ExpenseViewHolder(
            ItemExpenseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val expense = getItem(position)
        with(holder as ExpenseViewHolder) {
            with(binding) {
                val iContext = itemView.context
                txtCost.text = expense.cost.toFormatedString().plusCurrency(iContext)
                txtCost.setCustomIcon(
                    isIncome = expense.income,
                    inDrawable = R.drawable.ic_sell,
                    outDrawable = R.drawable.ic_buy
                )
                txtDes.text = bindDateWithInfo(
                    dHint = iContext.getString(R.string.date),
                    iHint = iContext.getString(R.string.shared_with),
                    date = expense.creationDate + "\n${iContext.getString(R.string.by)}: ${expense.createdBy}.",
                    info = expense.name
                )
                txtNote.text = bindNote(
                    nHint = iContext.getString(R.string.note),
                    note = expense.note
                )
                imgMenu.setOnClickListener {
                    it.inflateItemMenu(position)
                }
            }
            itemView.setOnClickListener {
                event.value = ExpenseEvent.OnListItemClick(position)
            }
        }
    }

    private fun View.inflateItemMenu(position: Int) {
        val popupMenu = PopupMenu(this@inflateItemMenu.context, this)
        popupMenu.apply {
            inflate(R.menu.expense_menu)
            menu.findItem(R.id.delete_menu).setRedTitle()
            setOnMenuItemClickListener { item ->
                when (item?.itemId) {
                    R.id.refresh_menu -> event.value = ExpenseEvent.OnMenuListRefresh
                    R.id.expand_menu -> event.value = ExpenseEvent.OnStartGetItem
                    R.id.delete_menu -> context.displayConfirmDialog(
                        title = R.string.delete_permanently
                    ) {
                        event.value = ExpenseEvent.OnMenuDeleteClick(position)
                    }

                    R.id.export_menu -> context.displayConfirmDialog(
                        title = R.string.export_summary
                    ) {
                        event.value = ExpenseEvent.OnMenuExportClick
                    }
                }
                false
            }
            show()
        }
    }
}
