package com.portfolio.core.data.data_source.model

import com.portfolio.core.domain.model.IngredientListing

data class IngredientListingSerializable(
    val name: String = "",
    val quantity: Float? = null,
    val unit: String = ""
)

fun IngredientListingSerializable.toIngredientListing(): IngredientListing {
    return IngredientListing(
        name = name,
        quantity = quantity,
        unit = unit
    )
}

fun IngredientListing.toIngredientListingSerializable(): IngredientListingSerializable {
    return IngredientListingSerializable(
        name = name,
        quantity = quantity,
        unit = unit
    )
}