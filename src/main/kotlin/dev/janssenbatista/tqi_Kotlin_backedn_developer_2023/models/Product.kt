package dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.models

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.ZonedDateTime
import java.util.*

@Entity
@Table(name = "tb_products")
data class Product(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID? = UUID.randomUUID(),
    @Column(length = 100, nullable = false, unique = true)
    var name: String,
    @Column(name = "measurement_unit", length = 5, nullable = false)
    var measurementUnit: String,
    @Column(name = "unit_price", nullable = false)
    var unitPrice: BigDecimal,
    @Column(name = "quantity_in_stock")
    var quantityInStock: Int,
    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    var category: Category,
    @Column(name = "created_at")
    val createdAt: ZonedDateTime = ZonedDateTime.now(),
    @Column(name = "updated_at")
    var updatedAt: ZonedDateTime = ZonedDateTime.now()
)
