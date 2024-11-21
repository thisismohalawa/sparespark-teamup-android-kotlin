package sparespark.teamup.transaction.itemlist.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import sparespark.teamup.R
import sparespark.teamup.core.internal.plusCurrency
import sparespark.teamup.core.internal.plusQuan
import sparespark.teamup.core.internal.toFormatedString
import sparespark.teamup.core.setDrawablePNType
import sparespark.teamup.data.model.balance.TransactionBalance
import sparespark.teamup.databinding.ItemBalanceBinding
import sparespark.teamup.databinding.ItemBalanceHorizontalBinding
import sparespark.teamup.transaction.itemlist.adapter.diffutil.TransactionBalanceDiffUtilCallback

class TransactionBalanceAdapter(
    private val isHorizontalBalanceView: Boolean
) :
    ListAdapter<TransactionBalance, RecyclerView.ViewHolder>(
        TransactionBalanceDiffUtilCallback()
    ) {

    inner class ItemBalanceViewHolder(var binding: ItemBalanceBinding) :
        RecyclerView.ViewHolder(
            binding.root
        )

    inner class ItemBalanceHorizontalViewHolder(var binding: ItemBalanceHorizontalBinding) :
        RecyclerView.ViewHolder(
            binding.root
        )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (isHorizontalBalanceView) ItemBalanceHorizontalViewHolder(
            ItemBalanceHorizontalBinding.inflate(inflater, parent, false)
        ) else ItemBalanceViewHolder(
            ItemBalanceBinding.inflate(inflater, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        if (isHorizontalBalanceView)
            (holder as ItemBalanceHorizontalViewHolder).setupBalanceHorizontalViewHolder(item)
        else
            (holder as ItemBalanceViewHolder).setupBalanceViewHolder(item)
    }


    private fun ItemBalanceViewHolder.setupBalanceViewHolder(item: TransactionBalance) {
        val iContext = this.itemView.context
        with(this.binding) {
            txtTitle.text = if (item.isCost == true) item.total.toFormatedString().plusCurrency(iContext)
            else item.total.toFormatedString().plusQuan(iContext)

            txtTitle.setDrawablePNType(
                income = item.total >= 0.0,
                context = iContext
            )
        }
    }

    private fun ItemBalanceHorizontalViewHolder.setupBalanceHorizontalViewHolder(item: TransactionBalance) {
        val iContext = this.itemView.context
        with(this.binding) {
            txtTitle.text = item.total.toFormatedString()

            txtDes.text = if (item.isSell == true) item.desRes?.let { iContext.sellLabelText(it) }
            else item.desRes?.let { iContext.buyLabelText(it) }
        }
    }

    private fun Context.sellLabelText(sRes: Int): String {
        return this.getString(R.string.sell) + "\\${this.getString(sRes)}"
    }

    private fun Context.buyLabelText(sRes: Int): String {
        return this.getString(R.string.buy) + "\\${this.getString(sRes)}"
    }
}