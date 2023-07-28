package dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.dtos

import java.util.UUID


data class ItemResponseDto (
    val itemId: UUID,
    val shoppingCartId: UUID,
    val productName: String,
    val quantity: Int
)