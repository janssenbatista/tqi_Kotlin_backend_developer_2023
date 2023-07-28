package dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.dtos

import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.enums.PaymentMethod
import java.math.BigDecimal
import java.time.ZonedDateTime
import java.util.*

data class CheckoutResponseDto(
    val shoppingCartId: UUID,
    val customerName: String,
    val totalValue: BigDecimal,
    val paymentMethod: PaymentMethod,
    val paidAt: ZonedDateTime
)