package sparespark.teamup.core

import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import sparespark.teamup.data.model.client.Client
import sparespark.teamup.data.model.product.Product
import sparespark.teamup.data.model.stock.Stock


internal fun AutoCompleteTextView.bindClients(
    list: List<Client>?,
    action: ((String) -> Unit)? = null
) {
    list?.let {
        if (it.isEmpty()) return

        val clientNames = mutableListOf<String>()
        for (i in it.indices) clientNames.add(it[i].name)

        val aa: ArrayAdapter<String> =
            ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item, clientNames)
        this.setAdapter(aa)
        this.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            action?.invoke(
                adapter.getItem(position).toString()
            )
        }
    }
}

internal fun AutoCompleteTextView.bindStockProduct(
    list: List<Product>?,
    action: ((String) -> Unit)? = null
) {
    list?.let {
        if (it.isEmpty()) return

        val clientNames = mutableListOf<String>()
        for (i in it.indices) clientNames.add("${it[i].name}\\${it[i].companyEntry.companyName}")

        val aa: ArrayAdapter<String> =
            ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item, clientNames)
        this.setAdapter(aa)
        this.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            action?.invoke(
                adapter.getItem(position).toString()
            )
        }
    }
}