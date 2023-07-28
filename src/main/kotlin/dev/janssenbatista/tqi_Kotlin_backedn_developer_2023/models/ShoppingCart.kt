package dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.models

import com.fasterxml.jackson.annotation.JsonIgnore
import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.enums.PaymentMethod
import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.enums.ShoppingCartStatus
import jakarta.persistence.*
import java.time.ZonedDateTime
import java.util.*

@Entity
@Table(name = "tb_shopping_carts")
data class ShoppingCart(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID? = null,
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    val customer: User,
    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    var status: ShoppingCartStatus = ShoppingCartStatus.WAITING_PAYMENT,
    @Enumerated(value = EnumType.STRING)
    @Column(name = "payment_method")
    var paymentMethod: PaymentMethod? = null,
    @JsonIgnore
    @OneToMany(mappedBy = "shoppingCart")
    val items: MutableList<Item> = mutableListOf(),
    @JsonIgnore
    @Column(name = "created_at", nullable = false)
    val createdAt: ZonedDateTime = ZonedDateTime.now(),
    @JsonIgnore
    @Column(name = "updated_at", nullable = false)
    var updatedAt: ZonedDateTime = ZonedDateTime.now()
)
