package dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.services

import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.dtos.CategoryRequestDto
import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.exceptions.CategoryAlreadyExistsException
import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.exceptions.CategoryNotFoundException
import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.models.Category
import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.repositories.CategoryRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.junit.jupiter.api.Test
import java.util.*

class CategoryServiceUnitTest {

    private val categoryRepository: CategoryRepository = mockk()
    private val categoryService: CategoryService = CategoryService(categoryRepository)
    private val categoryRequestDto = CategoryRequestDto(name = "category name")

    // save
    @Test
    fun `should be able to create a category`() {
        val createdCategory = Category(id = 1, name = categoryRequestDto.name)
        every { categoryRepository.findByName(any()) } returns Optional.empty()
        every { categoryRepository.save(any()) } returns createdCategory
        categoryService.save(categoryRequestDto)
        assertThat(createdCategory.id).isEqualTo(1)
        assertThat(createdCategory.name).isEqualTo(categoryRequestDto.name)
        assertThat(createdCategory.createdAt).isNotNull()
        assertThat(createdCategory.updatedAt).isNotNull()
        verify(exactly = 1) { categoryRepository.findByName(any()) }
        verify(exactly = 1) { categoryRepository.save(any()) }
    }

    @Test
    fun `should not be able to create a category with existing name`() {
        val category = Category(id = 1, name = categoryRequestDto.name)
        every { categoryRepository.findByName(any()) } returns Optional.of(category)
        assertThatExceptionOfType(CategoryAlreadyExistsException::class.java).isThrownBy {
            categoryService.save(categoryRequestDto)
        }.withMessage("category already exists")
        verify(exactly = 1) { categoryRepository.findByName(any()) }
        verify(exactly = 0) { categoryRepository.save(any()) }
    }

    // findById

    @Test
    fun `should be able to find a category`() {
        val category = Category(id = 1, name = "name")
        every { categoryRepository.findById(any()) } returns Optional.of(category)
        categoryService.findById(categoryId = category.id!!)
        verify(exactly = 1) { categoryRepository.findById(any()) }
    }

    @Test
    fun `should not be able to find a category with invalid id`() {
        val categoryId = 1
        every { categoryRepository.findById(categoryId) } returns Optional.empty()
        assertThatExceptionOfType(CategoryNotFoundException::class.java).isThrownBy {
            categoryService.findById(categoryId)
        }.withMessage("category not found")
        verify(exactly = 1) { categoryRepository.findById(categoryId) }
    }


    // update
    @Test
    fun `should be able to update a category`() {
        val category = Category(id = 1, name = "category name")
        val updatedCategory = Category(name = "updatedCategory")
        every { categoryRepository.findById(category.id!!) } returns Optional.of(category)
        every { categoryRepository.findByName(categoryRequestDto.name) } returns Optional.empty()
        every { categoryRepository.save(category) } returns updatedCategory
        categoryService.update(categoryId = category.id!!, categoryRequestDto)
        verify(exactly = 1) { categoryRepository.findById(category.id!!) }
        verify(exactly = 1) { categoryRepository.findByName(categoryRequestDto.name) }
        verify(exactly = 1) { categoryRepository.save(category) }
    }

    @Test
    fun `should not be able to update a category with invalid id`() {
        val categoryId = 1
        every { categoryRepository.findById(categoryId) } returns Optional.empty()
        assertThatExceptionOfType(CategoryNotFoundException::class.java).isThrownBy {
            categoryService.update(categoryId, categoryRequestDto)
        }.withMessage("category not found")
        verify(exactly = 1) { categoryRepository.findById(categoryId) }
        verify(exactly = 0) { categoryRepository.save(any()) }
    }

    @Test
    fun `should not be able to update a category when other category with the same name already exists`() {
        val category = Category(id = 1, name = "category name")
        val category2 = Category(id = 2, name = categoryRequestDto.name)
        every { categoryRepository.findById(category.id!!) } returns Optional.of(category)
        every { categoryRepository.findByName(category2.name) } returns Optional.of(category2)
        assertThatExceptionOfType(CategoryAlreadyExistsException::class.java).isThrownBy {
            categoryService.update(category.id!!, categoryRequestDto)
        }.withMessage("other category with the same name already exists")
        verify(exactly = 1) { categoryRepository.findById(category.id!!) }
        verify(exactly = 1) { categoryRepository.findByName(category2.name) }
        verify(exactly = 0) { categoryRepository.save(any()) }
    }

    // delete
    @Test
    fun `should be able to delete a category`() {
        val category = Category(id = 1, name = "category name")
        every { categoryRepository.findById(category.id!!) } returns Optional.of(category)
        every { categoryRepository.delete(category) } returns Unit
        categoryService.delete(category.id!!)
        verify(exactly = 1) { categoryRepository.findById(category.id!!) }
        verify(exactly = 1) { categoryRepository.delete(category) }
    }

    @Test
    fun `should not be able to delete a category with invalid id`() {
        val categoryId = 1
        every { categoryRepository.findById(categoryId) } returns Optional.empty()
        assertThatExceptionOfType(CategoryNotFoundException::class.java).isThrownBy {
            categoryService.delete(categoryId)
        }.withMessage("category not found")
        verify(exactly = 1) { categoryRepository.findById(categoryId) }
        verify(exactly = 0) { categoryRepository.save(any()) }
    }


}