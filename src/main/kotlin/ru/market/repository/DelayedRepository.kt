package ru.market.repository

import kotlinx.coroutines.delay
import shared.Currency
import shared.Product
import kotlin.random.Random
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.times

@OptIn(ExperimentalTime::class)
class DelayedRepository(val delayFrom: Duration, val delayTo: Duration, private val sourceRepository: AbstractRepository): AbstractRepository {
    init {
        require(delayFrom <= delayTo)
    }
    override suspend fun getCurrenciesByUserId(id: ULong): Set<Currency> {
        imitate()
        return sourceRepository.getCurrenciesByUserId(id)
    }

    override suspend fun setCurrenciesByUserId(id: ULong, preferredCurrencies: Set<Currency>) {
        imitate()
        sourceRepository.setCurrenciesByUserId(id, preferredCurrencies)
    }

    override suspend fun getProductsCount(): ULong {
        imitate()
        return sourceRepository.getProductsCount()
    }

    override suspend fun getProductById(id: ULong): Product? {
        imitate()
        return sourceRepository.getProductById(id)
    }

    override suspend fun addProduct(newProduct: Product) {
        imitate()
        sourceRepository.addProduct(newProduct)
    }

    private suspend fun imitate() {
        delay(delayFrom + Random.nextDouble() * (delayTo - delayFrom))
    }
}