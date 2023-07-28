package dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.dtos

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank

data class ItemRequestDto(
    @field:NotBlank(message = "product id cannot be blank")
    val productId: String,
    @field:Min(value = 1, message = "quantity cannot be less than 1")
    val quantity: Int
)