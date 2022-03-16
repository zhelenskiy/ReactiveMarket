package shared

import kotlinx.serialization.Serializable

@Serializable
sealed class MaybeProduct {
    @Serializable
    data class Just(val value: Product) : MaybeProduct()

    @Serializable
    object None : MaybeProduct()

    fun toNullable(): Product? = when (this) {
        is Just -> value
        None -> null
    }

    fun map(transform: (Product) -> Product): MaybeProduct = when (this) {
        is Just -> Just(transform(value))
        None -> None
    }
}

fun Product?.toMaybe() = when (this) {
    null -> MaybeProduct.None
    else -> MaybeProduct.Just(this)
}