package dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.services

import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.dtos.ProductRequestDto
import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.exceptions.ProductAlreadyExistsException
import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.exceptions.ProductNotFoundException
import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.models.Product
import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.repositories.ProductRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.ZonedDateTime
import java.util.*

@Service
class ProductService(private val productRepository: ProductRepository,
                     private val categoryService: CategoryService) {

    fun save(dto: ProductRequestDto): Product {
        val productAlreadyExists = productRepository.findByName(dto.name)
        if (productAlreadyExists.isPresent) {
            throw ProductAlreadyExistsException("product already exists")
        }
        val category = categoryService.findById(dto.categoryId)
        return productRepository.save(dto.toEntity(category))
    }

    fun findById(productId: UUID): Product {
        return productRepository.findById(productId).orElseThrow {
            ProductNotFoundException("product not found")
        }
    }

    fun findAll(pageable: Pageable): Page<Product> {
        return productRepository.findAll(pageable)
    }

    fun update(productId: UUID, dto: ProductRequestDto): Product {
        val product = productRepository.findById(productId).orElseThrow {
            ProductNotFoundException("product not found")
        }
        val otherProduct = productRepository.findByName(dto.name)
        if (otherProduct.isPresent && otherProduct.get() != product) {
            throw ProductAlreadyExistsException("other product with the same name already exists")
        }
        var category = categoryService.findById(dto.categoryId)
        product.apply {
            name = dto.name
            measurementUnit = dto.measurementUnit
            unitPrice = dto.unitPrice
            quantityInStock = dto.quantityInStock
            category = category
            updatedAt = ZonedDateTime.now()
        }
        return productRepository.save(product)
    }

    fun delete(productId: UUID) {
        val product = productRepository.findById(productId).orElseThrow {
            ProductNotFoundException("product not found")
        }
        productRepository.deleteById(product.id!!)
    }
}