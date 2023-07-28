package dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.controllers

import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.dtos.CategoryRequestDto
import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.models.Category
import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.services.CategoryService
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

@RestController
@RequestMapping("/categories")
@Tag(name = "Category")
@SecurityRequirement(name = "bearerAuth")
class CategoryController(private val categoryService: CategoryService) {

    @Operation(
        summary = "create a category",
        responses = [
            ApiResponse(description = "category created", responseCode = "201"),
            ApiResponse(description = "category already exists", responseCode = "409"),
        ]
    )
    @PostMapping
    fun createCategory(@RequestBody @Valid categoryRequestDto: CategoryRequestDto): ResponseEntity<Category> {
        val createdCategory = categoryService.save(categoryRequestDto)
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCategory)
    }

    @Operation(
        summary = "find a category by id",
        responses = [
            ApiResponse(description = "category found", responseCode = "200"),
            ApiResponse(description = "category not found", responseCode = "404"),
        ]
    )
    @GetMapping("/{categoryId}")
    fun findCategoryById(@PathVariable categoryId: Int): ResponseEntity<Category> {
        val foundCategory = categoryService.findById(categoryId)
        return ResponseEntity.ok(foundCategory)
    }

    @Operation(
        summary = "get all categories",
        responses = [
            ApiResponse(description = "get all categories", responseCode = "200"),
        ]
    )
    @GetMapping
    fun findAllCategories(@PageableDefault(
        page = 0,
        size = 20, sort = ["name"],
        direction = Sort.Direction.ASC) pageable: Pageable): ResponseEntity<Page<Category>> {
        return ResponseEntity.ok(categoryService.findAll(pageable))
    }

    @Operation(
        summary = "update a category by id",
        responses = [
            ApiResponse(description = "category updated", responseCode = "200"),
            ApiResponse(description = "category not found", responseCode = "404"),
            ApiResponse(description = "category name already exists", responseCode = "409"),
        ]
    )
    @PutMapping("/{categoryId}")
    fun updateCategory(@PathVariable categoryId: Int,
                       @RequestBody @Valid categoryRequestDto: CategoryRequestDto): ResponseEntity<Category> {
        val updatedCategory = categoryService.update(categoryId, categoryRequestDto)
        return ResponseEntity.ok(updatedCategory)
    }

    @Operation(
        summary = "delete a category by id",
        responses = [
            ApiResponse(description = "category updated", responseCode = "200"),
            ApiResponse(description = "category not found", responseCode = "404"),
            ApiResponse(description = "category has products associated", responseCode = "400"),
        ]
    )
    @DeleteMapping("/{categoryId}")
    fun deleteCategory(@PathVariable categoryId: Int): ResponseEntity<Any> {
        categoryService.delete(categoryId)
        return ResponseEntity.noContent().build()
    }
}