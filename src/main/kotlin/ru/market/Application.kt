package ru.market

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.*
import kotlinx.coroutines.runBlocking
import ru.market.repository.AbstractRepository
import ru.market.repository.DelayedRepository
import ru.market.repository.InMemoryRepository
import shared.Currency
import shared.Product
import kotlin.random.Random
import kotlin.random.nextULong
import kotlin.time.Duration.Companion.milliseconds

private fun AbstractRepository.stubData() = runBlocking {
    for (i in 1..100) {
        addProduct(
            Product(
                "Product #$i",
                Currency.values().filter { Random.nextBoolean() }.associateWith { Random.nextULong(10000U) }
            )
        )
    }
}

fun main() {
    val repository = DelayedRepository(10.milliseconds, 100.milliseconds, InMemoryRepository().apply { stubData() }) // to simulate ping
    embeddedServer(Netty, port = 8080) {
        install(ContentNegotiation) {
            json()
        }
        configureRouting(repository)
    }.start(wait = true)
}
