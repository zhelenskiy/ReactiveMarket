package shared

import kotlinx.serialization.Serializable

@Serializable
data class Product(val name: String, val prices: Map<Currency, ULong>)

