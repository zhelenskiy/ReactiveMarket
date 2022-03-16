package ru.market.repository

import shared.Currency
import shared.Product

interface AbstractRepository {
    suspend fun getCurrenciesByUserId(id: ULong): Set<Currency>
    suspend fun setCurrenciesByUserId(id: ULong, preferredCurrencies: Set<Currency>)

    suspend fun getProductsCount(): ULong
    suspend fun getProductById(id: ULong): Product?
    suspend fun addProduct(newProduct: Product)
}

