package sparespark.teamup.stock

import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import sparespark.teamup.R
import sparespark.teamup.data.model.PositionSelectItem

open class BaseStockListView : Fragment() {
    protected fun TextView.updateHintTitle(list: List<Int>) {
        var title = ""
        list.forEach { title += getString(it) + "\\" }
        text = title
    }

    protected fun RecyclerView.updateSelectItem(it: PositionSelectItem) {
        val itemView = this@updateSelectItem.findViewHolderForAdapterPosition(it.pos)?.itemView
        val contentLayout = itemView?.findViewById<LinearLayout>(R.id.root_layout)
        if (it.isSelect) contentLayout?.setBackgroundResource(R.drawable.item_rounded_layout_gray)
        else contentLayout?.setBackgroundResource(R.drawable.item_rounded_layout_green)
    }
}