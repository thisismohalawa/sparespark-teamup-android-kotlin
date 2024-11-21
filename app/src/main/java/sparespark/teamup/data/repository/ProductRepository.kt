package sparespark.teamup.data.repository

import sparespark.teamup.core.wrapper.Result
import sparespark.teamup.data.model.product.Product

interface ProductRepository {
    suspend fun getProductList(localOnly: Unit? = null): Result<Exception, List<Product>>
    suspend fun updateProduct(product: Product): Result<Exception, Unit>
    suspend fun deleteProduct(
        itemId: String? = null,
        itemsIds: List<String>? = null,
    ): Result<Exception, Unit>

    suspend fun clearListCacheTime(): Result<Exception, Unit>

}
