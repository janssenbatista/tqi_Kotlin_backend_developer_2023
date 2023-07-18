package dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.models

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import java.time.ZonedDateTime

@Entity
@Table(name = "tb_categories")
data class Category(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cat_seq_gen")
    @SequenceGenerator(name = "cat_seq_gen",
        sequenceName = "tb_categories_seq", allocationSize = 1)
    val id: Int? = null,
    @Column(length = 50, nullable = false)
    var name: String,
    @Column(name = "created_at")
    val createdAt: ZonedDateTime = ZonedDateTime.now(),
    @Column(name = "updated_at")
    var updatedAt: ZonedDateTime = ZonedDateTime.now(),
    @JsonIgnore
    @OneToMany(mappedBy = "category")
    val products: MutableList<Product> = mutableListOf()
)
