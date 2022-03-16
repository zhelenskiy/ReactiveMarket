package shared

import kotlinx.serialization.Serializable

@Serializable
enum class Currency(val symbol: String) {
    Ruble("₽") {
        override fun format(price: ULong): String = "$price$symbol"
    },
    Dollar("$"),
    Euro("€"),
    Pound("£"),
    Hryvnia("₴");

    open fun format(price: ULong): String = symbol + price
}