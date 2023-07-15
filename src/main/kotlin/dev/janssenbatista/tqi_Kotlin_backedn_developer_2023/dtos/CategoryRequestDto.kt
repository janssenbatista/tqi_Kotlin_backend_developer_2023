package dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.dtos

import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.models.Category
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class CategoryRequestDto(
    @field:NotBlank
    @field:Size(max = 50, message = "category must contain a maximum of 50 characters")
    val name: String
) {
    fun toEntity(): Category =
        Category(
            name = this.name
        )
}