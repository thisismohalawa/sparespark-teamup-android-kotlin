package sparespark.teamup.core

import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import sparespark.teamup.data.model.city.City
import sparespark.teamup.data.model.client.Client
import sparespark.teamup.data.model.company.Company
import sparespark.teamup.data.model.product.Product
import sparespark.teamup.data.model.stock.Stock

internal fun Spinner.bindCityList(
    list: List<City>?,
    selectTitle: String? = null,
    selectAction: ((Int?) -> Unit)
) {

    list?.let {
        if (it.isEmpty()) return

        val hasSelectTitle = selectTitle != null
        val cityNames = mutableListOf<String>()

        if (hasSelectTitle) cityNames.add(selectTitle ?: "")

        for (i in it.indices) cityNames.add(it[i].name)

        val aa: ArrayAdapter<String> =
            ArrayAdapter(context, android.R.layout.simple_spinner_item, cityNames)
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        this.adapter = aa
        this.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>, view: View?, position: Int, id: Long
            ) {

                if (hasSelectTitle) {
                    when (position) {
                        0 -> selectAction.invoke(null)
                        else -> selectAction.invoke(position - 1)
                    }
                } else selectAction.invoke(position)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        this.visible(true)
    }
}

internal fun Spinner.bindClientList(
    list: List<Client>?,
    selectTitle: String? = null,
    selectAction: ((Int?) -> Unit)
) {

    list?.let {
        if (it.isEmpty()) return

        val hasSelectTitle = selectTitle != null
        val clientNames = mutableListOf<String>()

        if (hasSelectTitle) clientNames.add(selectTitle ?: "")

        for (i in it.indices) clientNames.add(it[i].name)

        val aa: ArrayAdapter<String> =
            ArrayAdapter(context, android.R.layout.simple_spinner_item, clientNames)
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        this.adapter = aa
        this.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>, view: View?, position: Int, id: Long
            ) {

                if (hasSelectTitle) {
                    when (position) {
                        0 -> selectAction.invoke(null)
                        else -> selectAction.invoke(position - 1)
                    }
                } else selectAction.invoke(position)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        this.visible(true)
    }
}

internal fun Spinner.bindCompanyList(
    list: List<Company>?,
    selectTitle: String? = null,
    selectAction: ((Int?) -> Unit)
) {

    list?.let {
        if (it.isEmpty()) return

        val hasSelectTitle = selectTitle != null
        val cityNames = mutableListOf<String>()

        if (hasSelectTitle) cityNames.add(selectTitle ?: "")

        for (i in it.indices) cityNames.add(it[i].name)

        val aa: ArrayAdapter<String> =
            ArrayAdapter(context, android.R.layout.simple_spinner_item, cityNames)
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        this.adapter = aa
        this.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>, view: View?, position: Int, id: Long
            ) {

                if (hasSelectTitle) {
                    when (position) {
                        0 -> selectAction.invoke(null)
                        else -> selectAction.invoke(position - 1)
                    }
                } else selectAction.invoke(position)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        this.visible(true)
    }
}

internal fun Spinner.bindProductList(
    list: List<Product>?,
    selectTitle: String? = null,
    selectAction: ((Int?) -> Unit)
) {

    list?.let {
        if (it.isEmpty()) return

        val hasSelectTitle = selectTitle != null
        val companyNames = mutableListOf<String>()

        if (hasSelectTitle) companyNames.add(selectTitle ?: "")

        for (i in it.indices) companyNames.add(it[i].name)

        val aa: ArrayAdapter<String> =
            ArrayAdapter(context, android.R.layout.simple_spinner_item, companyNames)
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        this.adapter = aa
        this.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>, view: View?, position: Int, id: Long
            ) {

                if (hasSelectTitle) {
                    when (position) {
                        0 -> selectAction.invoke(null)
                        else -> selectAction.invoke(position - 1)
                    }
                } else selectAction.invoke(position)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        this.visible(true)
    }
}

internal fun Spinner.bindStockList(
    list: List<Stock>,
    selectAction: ((Int) -> Unit)? = null
) {
    list.let {
        if (it.isEmpty()) {
            this.adapter = null
            this.visible(false)
            return
        }
        val pairs = mutableListOf<String>()

        for (i in it.indices)
            pairs.add("${it[i].productEntry.name}\\${it[i].productEntry.company}")


        val aa: ArrayAdapter<String> =
            ArrayAdapter(context, android.R.layout.simple_spinner_item, pairs)
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        this.adapter = aa
        this.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>, view: View?, position: Int, id: Long
            ) {
                selectAction?.invoke(position)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        this.visible(true)
    }
}


internal fun Spinner.selectQuery(query: String?) {
    if (this.count == 0 ||
        query.isNullOrBlank()
    ) return

    for (i in 0 until this.count) {
        if (this.getItemAtPosition(i) == query) {
            this.setSelection(i)
            break
        }
    }
}