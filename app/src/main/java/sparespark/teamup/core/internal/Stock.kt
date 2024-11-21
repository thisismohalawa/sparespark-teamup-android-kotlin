package sparespark.teamup.core.internal

import sparespark.teamup.data.model.AssetEntry
import sparespark.teamup.data.model.ClientEntry
import sparespark.teamup.data.model.ProductEntry
import sparespark.teamup.data.model.statics.StockStatics
import sparespark.teamup.data.model.stock.Stock


internal fun newStock(isSell: Boolean = false) = Stock(
    id = "",
    creationDate = "",
    datasourceId = 0,
    productEntry = ProductEntry(),
    clientEntry = ClientEntry(),
    assetEntry = AssetEntry(),
    note = "",
    sell = isSell,
    temp = false,
    updateBy = "",
    updateDate = ""
)

internal fun Stock.toShareText(): String {
    return "Type: ${this.sell.toTypeText()}\n" +
            "Date: ${this.creationDate}\n" +
            "Client: ${this.clientEntry.name}\n" +
            "Quantity: ${this.assetEntry.quantity}\n" +
            "City: ${this.clientEntry.city}\n" +
            "Note: ${this.note}."
}

internal fun List<StockStatics>.getDuplicatedIndex(
    product: String,
    company: String
): Int {
    var dIndex = -1
    forEachIndexed { index, element ->
        if (element.product == product &&
            element.company == company
        ) {
            dIndex = index
        }
    }
    return dIndex
}
