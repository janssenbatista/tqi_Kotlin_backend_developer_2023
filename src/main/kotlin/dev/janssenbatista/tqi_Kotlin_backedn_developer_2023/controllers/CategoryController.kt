package dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.controllers

import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.dtos.CategoryRequestDto
import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.models.Category
import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.services.CategoryService
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
class CategoryController(private val categoryService: CategoryService) {

    @PostMapping
    fun createCategory(@RequestBody @Valid categoryRequestDto: CategoryRequestDto): ResponseEntity<Category> {
        val createdCategory = categoryService.save(categoryRequestDto)
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCategory)
    }

    @GetMapping("/{categoryId}")
    fun findCategoryById(@PathVariable categoryId: Int): ResponseEntity<Category> {
        val foundCategory = categoryService.findById(categoryId)
        return ResponseEntity.ok(foundCategory)
    }

    @GetMapping
    fun findAllCategories(@PageableDefault(
        page = 0,
        size = 20, sort = ["name"],
        direction = Sort.Direction.ASC) pageable: Pageable): ResponseEntity<Page<Category>> {
        return ResponseEntity.ok(categoryService.findAll(pageable))
    }

    @PutMapping("/{categoryId}")
    fun updateCategory(@PathVariable categoryId: Int,
                       @RequestBody @Valid categoryRequestDto: CategoryRequestDto): ResponseEntity<Category> {
        val updatedCategory = categoryService.update(categoryId, categoryRequestDto)
        return ResponseEntity.ok(updatedCategory)
    }

    @DeleteMapping("/{categoryId}")
    fun deleteCategory(@PathVariable categoryId: Int): ResponseEntity<Any> {
        categoryService.delete(categoryId)
        return ResponseEntity.noContent().build()
    }
}