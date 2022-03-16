package ru.market.repository

import io.ktor.util.collections.*
import shared.Currency
import shared.Product
import java.util.concurrent.ConcurrentHashMap

class InMemoryRepository: AbstractRepository {
    private val preferredCurrencies: MutableMap<ULong, Set<Currency>> = ConcurrentHashMap()

    override suspend fun getCurrenciesByUserId(id: ULong): Set<Currency> = preferredCurrencies[id] ?: emptySet()

    override suspend fun setCurrenciesByUserId(id: ULong, preferredCurrencies: Set<Currency>) {
        when {
            preferredCurrencies.isEmpty() -> this.preferredCurrencies.remove(id)
            else -> this.preferredCurrencies[id] = preferredCurrencies
        }
    }

    private val products: MutableList<Product> = ConcurrentList()

    override suspend fun getProductsCount(): ULong = products.size.toULong()

    override suspend fun getProductById(id: ULong): Product? =
        if (id <= Int.MAX_VALUE.toULong()) products.getOrNull(id.toInt()) else null

    override suspend fun addProduct(newProduct: Product) {
        products.add(newProduct)
    }
}