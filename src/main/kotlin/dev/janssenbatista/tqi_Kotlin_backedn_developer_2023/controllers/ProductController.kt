package dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.controllers

import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.dtos.ProductRequestDto
import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.models.Product
import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.services.ProductService
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/products")
class ProductController(private val productService: ProductService) {

    @PostMapping
    fun createProduct(@RequestBody @Valid dto: ProductRequestDto): ResponseEntity<Product> {
        val createdProduct = productService.save(dto)
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct)
    }

    @GetMapping
    fun findAllProducts(@PageableDefault(
        page = 0,
        size = 10, sort = ["name"],
        direction = Sort.Direction.ASC) pageable: Pageable): ResponseEntity<Page<Product>> {
        val productsPage = productService.findAll(pageable)
        return ResponseEntity.ok(productsPage)
    }

    @GetMapping("/{productId}")
    fun findProductById(@PathVariable productId: UUID): ResponseEntity<Product> {
        val foundProduct = productService.findById(productId)
        return ResponseEntity.ok(foundProduct)
    }

    @PutMapping("/{productId}")
    fun updateProduct(@PathVariable productId: UUID,
                      @RequestBody @Valid dto: ProductRequestDto): ResponseEntity<Product> {
        val updatedProduct = productService.update(productId, dto)
        return ResponseEntity.ok(updatedProduct)
    }

    @DeleteMapping("/{productId}")
    fun deleteProduct(@PathVariable productId: UUID): ResponseEntity<Product> {
        productService.delete(productId)
        return ResponseEntity.noContent().build()
    }
}