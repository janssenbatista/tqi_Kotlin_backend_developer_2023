package dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.dtos

import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.enums.PaymentMethod

data class CheckoutRequestDto(
    val paymentMethod: PaymentMethod
)