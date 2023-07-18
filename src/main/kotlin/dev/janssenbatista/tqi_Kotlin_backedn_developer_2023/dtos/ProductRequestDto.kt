package dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.dtos

import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.models.Category
import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.models.Product
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.math.BigDecimal
import java.time.ZonedDateTime

data class ProductRequestDto(
    @field:NotBlank(message = "name cannot be blank")
    @field:Size(max = 100, message = "name must contain a maximum of 100 characters")
    val name: String,
    @field:NotBlank(message = "measurementUnit cannot be blank")
    @field:Size(max = 100, message = "measurementUnit must contain a maximum of 5 characters")
    val measurementUnit: String,
    @field:DecimalMin(value = "0.0", inclusive = false, message = "unitPrice must be greater than 0.0")
    val unitPrice: BigDecimal,
    @field:Min(value = 1, message = "quantityInStock must be greater than 0")
    val quantityInStock: Int,
    @field:Min(value = 1, message = "quantityInStock must be greater than 0")
    val categoryId: Int
) {
    fun toEntity(category: Category): Product =
        Product(
            name = name,
            measurementUnit = measurementUnit,
            unitPrice = unitPrice,
            quantityInStock = quantityInStock,
            category = category,
            createdAt = ZonedDateTime.now(),
            updatedAt = ZonedDateTime.now())
}