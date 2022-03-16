package ru.market

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.pipeline.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import ru.market.repository.AbstractRepository
import shared.Currency
import shared.Product
import shared.toMaybe


fun Application.configureRouting(repository: AbstractRepository) {
    routing {
        controlProducts(repository)
        controlCurrency(repository)
    }
}

private fun Routing.controlProducts(repository: AbstractRepository) {
    get("/getProductsCount") {
        call.respond(repository.getProductsCount().toString())
    }
    get("/getProductById/{id}") {
        val indexStr = call.parameters["id"] ?: return@get emptyRequestError()
        val index = indexStr.toULongOrNull() ?: return@get notIdError(indexStr)
        call.respond(repository.getProductById(index)?.let { product ->
            val userId = call.request.queryParameters["userId"]?.toULongOrNull() ?: return@let product
            val currencies = repository.getCurrenciesByUserId(userId).takeIf { it.isNotEmpty() } ?: return@let product
            Product(product.name, product.prices.filterKeys { it in currencies })
        }.toMaybe())
    }
    post("/addProduct") {
        val product = try {
            call.receive<Product>()
        } catch (_: ContentTransformationException) {
            return@post call.respond(HttpStatusCode.BadRequest, "Invalid product")
        }
        repository.addProduct(product)
        call.respond(HttpStatusCode.Accepted)
    }
}

private fun Routing.controlCurrency(repository: AbstractRepository) {
    get("/getCurrenciesByUserId/{id}") {
        val idStr = call.parameters["id"] ?: return@get emptyRequestError()
        val id = idStr.toULongOrNull() ?: return@get notIdError(idStr)
        call.respond(repository.getCurrenciesByUserId(id))
    }
    put("/setCurrenciesByUserId/{id}") {
        val idStr = call.parameters["id"] ?: return@put emptyRequestError()
        val id = idStr.toULongOrNull() ?: return@put notIdError(idStr)
        val currencies = try {
            val string = call.receive<String>()
            Json.decodeFromString<Set<Currency>>(string)
        } catch (_: ContentTransformationException) {
            return@put call.respond(HttpStatusCode.BadRequest, "Invalid currency")
        }
        repository.setCurrenciesByUserId(id, currencies)
        call.respond(HttpStatusCode.Accepted)
    }
}

private suspend fun PipelineContext<Unit, ApplicationCall>.notIdError(idStr: String) {
    call.respond(HttpStatusCode.BadRequest, "$idStr is not an id")
}

private suspend fun PipelineContext<Unit, ApplicationCall>.emptyRequestError() {
    call.respond(HttpStatusCode.BadRequest, "Empty request")
}
