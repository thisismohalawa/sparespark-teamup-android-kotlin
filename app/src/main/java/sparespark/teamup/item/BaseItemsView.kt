package sparespark.teamup.item

import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import sparespark.teamup.R
import sparespark.teamup.core.toFormatedString
import sparespark.teamup.core.plusCurrency
import sparespark.teamup.core.plusQuan
import sparespark.teamup.core.setDrawablePNType
import sparespark.teamup.data.model.ISelect
import sparespark.teamup.data.model.statics.StaticsStates
import sparespark.teamup.data.model.statics.LStatics
import sparespark.teamup.databinding.ItemStaticsBinding

abstract class BaseItemsView : Fragment() {

    abstract fun setupListAdapter()

    protected fun TextView.updateHintTitle(list: List<Int>) {
        var title = ""
        list.forEach { title += getString(it) + "\\" }
        text = title
    }

    protected fun ItemStaticsBinding.updateListStatics(it: LStatics) {
        this.apply {
            itemBalance.txtHint.text = when (it.listSrcResId) {
                StaticsStates.CONNECTING -> getString(R.string.connecting)
                StaticsStates.UN_AUTH -> getString(R.string.unauthorized)
                StaticsStates.DISABLE -> getString(R.string.disable)
                StaticsStates.NOT_PERMITTED -> getString(R.string.not_permitted)
                StaticsStates.DEACTIVATED -> getString(R.string.deactivated)
                StaticsStates.SUCCESS -> getString(it.listSrcResId.value)
            }
            txtPrice.text = it.totalCost.toFormatedString().plusCurrency(context)
            txtQuantity.text = it.totalQuantity.toFormatedString().plusQuan(context)

            txtPrice.setDrawablePNType(
                total = it.totalCost, requireContext()
            )
            txtQuantity.setDrawablePNType(
                total = it.totalQuantity, requireContext()
            )
        }
    }

    protected fun RecyclerView.updateSelectItem(it: ISelect) {
        val itemView = this@updateSelectItem.findViewHolderForAdapterPosition(it.pos)?.itemView
        val contentLayout = itemView?.findViewById<LinearLayout>(R.id.content_layout)
        if (it.isSelect) contentLayout?.setBackgroundResource(R.drawable.item_rounded_layout_gray)
        else contentLayout?.setBackgroundResource(R.drawable.item_rounded_layout_green)
    }
}