package dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.repositories

import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.models.Item
import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.models.ShoppingCart
import jakarta.transaction.Transactional
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional
import java.util.UUID

interface ItemRepository : JpaRepository<Item, UUID> {

    fun findAllByShoppingCartId(id: UUID): List<Item>

    fun findByShoppingCartIdAndProductId(shoppingCartId: UUID, productId: UUID): Optional<Item>
}