package dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.repositories

import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.models.ShoppingCart
import jakarta.transaction.Transactional
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.math.BigDecimal
import java.util.*

interface ShoppingCartRepository : JpaRepository<ShoppingCart, UUID> {

    @Transactional
    @Query("""
        SELECT
            sum(tp.unit_price * ti.quantity)
        FROM
            tb_shopping_carts tsc
        JOIN tb_items ti ON tsc.id = ti.shopping_cart_id
        JOIN tb_products tp ON ti.product_id = tp.id WHERE tsc.id = :shoppingCartId;
    """, nativeQuery = true)
    fun getTotal(shoppingCartId: UUID): BigDecimal
}

