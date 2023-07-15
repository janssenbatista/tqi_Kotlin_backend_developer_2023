package dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.repositories

import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.models.Category
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional


interface CategoryRepository : JpaRepository<Category, Int> {
    fun findByName(name: String): Optional<Category>
}