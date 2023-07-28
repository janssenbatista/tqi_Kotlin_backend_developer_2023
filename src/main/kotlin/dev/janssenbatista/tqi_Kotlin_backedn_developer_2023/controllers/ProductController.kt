package dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.controllers

import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.dtos.ProductRequestDto
import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.models.Product
import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.services.ProductService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
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
@Tag(name = "Product")
@SecurityRequirement(name = "bearerAuth")
class ProductController(private val productService: ProductService) {

    @Operation(
        summary = "create a product",
        responses = [
            ApiResponse(description = "product already exists", responseCode = "409"),
            ApiResponse(description = "product created", responseCode = "201")
        ]
    )
    @PostMapping
    fun createProduct(@RequestBody @Valid dto: ProductRequestDto): ResponseEntity<Product> {
        val createdProduct = productService.save(dto)
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct)
    }

    @Operation(
        summary = "get all products",
        responses = [
            ApiResponse(description = "list of products", responseCode = "200")
        ]
    )
    @GetMapping
    fun findAllProducts(@PageableDefault(
        page = 0,
        size = 10, sort = ["name"],
        direction = Sort.Direction.ASC) pageable: Pageable): ResponseEntity<Page<Product>> {
        val productsPage = productService.findAll(pageable)
        return ResponseEntity.ok(productsPage)
    }

    @Operation(
        summary = "find product by id",
        responses = [
            ApiResponse(description = "product found", responseCode = "200"),
            ApiResponse(description = "product not found", responseCode = "404")
        ]
    )
    @GetMapping("/{productId}")
    fun findProductById(@PathVariable productId: UUID): ResponseEntity<Product> {
        val foundProduct = productService.findById(productId)
        return ResponseEntity.ok(foundProduct)
    }

    @Operation(
        summary = "update a product by id",
        responses = [
            ApiResponse(description = "product updated", responseCode = "200"),
            ApiResponse(description = "product not found", responseCode = "404"),
            ApiResponse(description = "product already exists", responseCode = "409")
        ]
    )
    @PutMapping("/{productId}")
    fun updateProduct(@PathVariable productId: UUID,
                      @RequestBody @Valid dto: ProductRequestDto): ResponseEntity<Product> {
        val updatedProduct = productService.update(productId, dto)
        return ResponseEntity.ok(updatedProduct)
    }

    @Operation(
        summary = "delete a product by id",
        responses = [
            ApiResponse(description = "product deleted", responseCode = "204"),
            ApiResponse(description = "product not found", responseCode = "404")
        ]
    )
    @DeleteMapping("/{productId}")
    fun deleteProduct(@PathVariable productId: UUID): ResponseEntity<Product> {
        productService.delete(productId)
        return ResponseEntity.noContent().build()
    }
}