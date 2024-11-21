package sparespark.teamup.core.internal

import sparespark.teamup.data.model.CompanyEntry
import sparespark.teamup.data.model.product.Product
import sparespark.teamup.data.model.product.RemoteProduct
import sparespark.teamup.data.room.product.RoomProduct

internal val RoomProduct.toProduct: Product
    get() = Product(
        id = this.id,
        name = this.name,
        companyEntry = this.companyEntry
    )
internal val RemoteProduct.toProduct: Product
    get() = Product(
        id = this.id ?: "",
        name = this.name ?: "",
        companyEntry = this.companyEntry ?: CompanyEntry()
    )
internal val Product.toRemoteProduct: RemoteProduct
    get() = RemoteProduct(
        id = this.id,
        name = this.name,
        companyEntry = this.companyEntry
    )
internal val Product.toRoomProduct: RoomProduct
    get() = RoomProduct(
        id = this.id,
        name = this.name,
        companyEntry = this.companyEntry
    )

internal fun List<RoomProduct>.toProductList(): List<Product> = this.flatMap {
    listOf(it.toProduct)
}
