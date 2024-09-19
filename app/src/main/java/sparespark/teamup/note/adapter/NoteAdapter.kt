package sparespark.teamup.note.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import sparespark.teamup.R
import sparespark.teamup.core.bindDateWithInfo
import sparespark.teamup.core.displayConfirmDialog
import sparespark.teamup.core.setRedTitle
import sparespark.teamup.data.model.note.Note
import sparespark.teamup.databinding.ItemNoteBinding
import sparespark.teamup.note.NoteListEvent

class NoteAdapter(
    val event: MutableLiveData<NoteListEvent> = MutableLiveData()
) : ListAdapter<Note, RecyclerView.ViewHolder>(NoteDiffUtilCallback()) {

    inner class NoteViewHolder(var binding: ItemNoteBinding) : RecyclerView.ViewHolder(
        binding.root
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return NoteViewHolder(
            ItemNoteBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val note = getItem(position)
        with(holder as NoteViewHolder) {
            with(binding) {
                txtTitle.text = note.title
                txtDes.text = bindDateWithInfo(
                    dHint = itemView.context.getString(R.string.date),
                    iHint = itemView.context.getString(R.string.by),
                    date = note.creationDate,
                    info = note.createdBy
                )
            }
            itemView.setOnClickListener {
                it.inflateItemMenu(note, position)
            }
        }
    }

    private fun View.inflateItemMenu(note: Note, position: Int) {
        val popupMenu = PopupMenu(this@inflateItemMenu.context, this@inflateItemMenu)
        popupMenu.apply {
            inflate(R.menu.note_menu)
            menu.findItem(R.id.delete_menu).setRedTitle()
            setOnMenuItemClickListener { item ->
                when (item?.itemId) {
                    R.id.refresh_menu -> event.value = NoteListEvent.OnMenuNoteListRefresh
                    R.id.delete_menu -> context.displayConfirmDialog(
                        title = R.string.delete_permanently
                    ) {
                        event.value = NoteListEvent.OnMenuNoteListDelete(
                            pos = position
                        )
                    }

                    R.id.full_page_menu ->
                        context.displayConfirmDialog(
                            title = R.string.note,
                            msg = "${note.title}.\" ${note.createdBy} ${note.creationDate}.",
                            pAction = {
                            }
                        )

                    R.id.update_item_menu -> event.value =
                        NoteListEvent.OnNoteItemClick(pos = position)
                }
                false
            }
            show()
        }
    }
}