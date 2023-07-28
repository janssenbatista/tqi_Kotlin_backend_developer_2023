package dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.dtos

import java.util.UUID

data class ShoppingCartResponseDto(
    val shoppingCartId: UUID,
    val customerId: UUID
)