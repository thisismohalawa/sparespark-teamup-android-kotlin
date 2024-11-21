package sparespark.teamup.core

import android.content.Context
import android.widget.TextView
import sparespark.teamup.R

internal fun bindDataWithHint(
    hint: String,
    data: String
): String =
    if (data.isNotBlank()) "$hint: $data"
    else ""

internal fun bindDataWithHint(
    mHint: String,
    mData: String,
    sHint: String,
    sData: String,
): String = "$mHint: $mData" + if (sData.isNotBlank()) "\n$sHint: $sData"
else mData + if (sData.isNotBlank())
    "\n$sHint $sData" else ""

internal fun TextView.bindHistory(
    updateBy: String, updateDate: String, context: Context
) {
    if (updateDate.isNotBlank()) {
        this.text =
            if (updateBy.isNotBlank()) "${context.getString(R.string.update_by)}: $updateBy " + updateDate else updateDate
        this.visible(true)
    } else this.visible(false)
}