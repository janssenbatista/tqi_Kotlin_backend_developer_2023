package dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.models

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import java.time.ZonedDateTime
import java.util.UUID

@Entity
@Table(name = "tb_items")
data class Item(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,
    @ManyToOne
    @JoinColumn(name = "shopping_cart_id", nullable = false)
    var shoppingCart: ShoppingCart,
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    val product: Product,
    @Column(nullable = false)
    var quantity: Int,
    @JsonIgnore
    @Column(name = "created_at", nullable = false)
    val createdAt: ZonedDateTime = ZonedDateTime.now(),
    @JsonIgnore
    @Column(name = "updated_at", nullable = false)
    var updatedAt: ZonedDateTime = ZonedDateTime.now()
)