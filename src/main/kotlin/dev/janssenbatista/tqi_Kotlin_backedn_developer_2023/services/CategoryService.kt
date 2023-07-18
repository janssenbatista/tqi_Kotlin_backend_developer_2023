package dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.services

import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.dtos.CategoryRequestDto
import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.exceptions.CategoryAlreadyExistsException
import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.exceptions.CategoryNotFoundException
import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.exceptions.ConstraintViolationException
import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.models.Category
import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.repositories.CategoryRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.time.ZonedDateTime

@Service
class CategoryService(private val categoryRepository: CategoryRepository) {

    fun save(categoryRequestDto: CategoryRequestDto): Category {
        val categoryAlreadyExists = categoryRepository.findByName(categoryRequestDto.name)
        if (categoryAlreadyExists.isPresent) {
            throw CategoryAlreadyExistsException("category already exists")
        }
        return categoryRepository.save(categoryRequestDto.toEntity())
    }

    fun findById(categoryId: Int): Category {
        val category = categoryRepository.findById(categoryId).orElseThrow {
            throw CategoryNotFoundException("category not found")
        }
        return category
    }

    fun findAll(pageable: Pageable): Page<Category> {
        return categoryRepository.findAll(pageable)
    }

    fun update(categoryId: Int, categoryRequestDto: CategoryRequestDto): Category {
        val category = categoryRepository.findById(categoryId).orElseThrow {
            throw CategoryNotFoundException("category not found")
        }
        val categoryAlreadyExists = categoryRepository.findByName(categoryRequestDto.name)
        if (categoryAlreadyExists.isPresent && category != categoryAlreadyExists.get()) {
            throw CategoryAlreadyExistsException("other category with the same name already exists")
        }
        category.apply {
            name = categoryRequestDto.name
            updatedAt = ZonedDateTime.now()
        }
        return categoryRepository.save(category)
    }

    fun delete(categoryId: Int) {
        val category = categoryRepository.findById(categoryId).orElseThrow {
            throw CategoryNotFoundException("category not found")
        }
        if (category.products.isNotEmpty()) {
            throw ConstraintViolationException("cannot delete this category because are products associated")
        }
        categoryRepository.delete(category)
    }

}