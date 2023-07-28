package dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.dtos

import jakarta.validation.constraints.NotBlank

data class ShoppingCartDto(
    @field:NotBlank(message = "customerId cannot be blank")
    val customerId: String
)