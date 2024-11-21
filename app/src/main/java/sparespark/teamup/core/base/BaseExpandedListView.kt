package sparespark.teamup.core.base

import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import sparespark.teamup.R
import sparespark.teamup.data.model.PositionSelectItem

abstract class BaseExpandedListView : Fragment() {

    protected fun RecyclerView.updateSelectItem(it: PositionSelectItem) {
        val itemView = this@updateSelectItem.findViewHolderForAdapterPosition(it.pos)?.itemView
        val contentLayout = itemView?.findViewById<RelativeLayout>(R.id.root_layout)
        if (it.isSelect) contentLayout?.setBackgroundResource(R.drawable.item_rounded_layout_background)
        else contentLayout?.setBackgroundResource(R.drawable.item_rounded_layout_green)
    }
}