package dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.dtos.CategoryRequestDto
import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.exceptions.CategoryAlreadyExistsException
import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.exceptions.CategoryNotFoundException
import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.models.Category
import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.security.WebSecurity
import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.services.CategoryService
import io.mockk.every
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.data.domain.Page
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.time.ZonedDateTime

@WebMvcTest(CategoryController::class)
@Import(WebSecurity::class)
class CategoryControllerUnitTest {

    @Autowired
    private lateinit var mapper: ObjectMapper

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockkBean
    private lateinit var categoryService: CategoryService

    private lateinit var dto: CategoryRequestDto
    private lateinit var dtoAsString: String
    private lateinit var category: Category

    @BeforeEach
    fun setup() {
        dto = CategoryRequestDto(name = "Category name")
        dtoAsString = mapper.writeValueAsString(dto)
        category = Category(
            id = 1,
            name = dto.name,
            createdAt = ZonedDateTime.now(),
            updatedAt = ZonedDateTime.now(),
            products = listOf()
        )
    }


    @Test
    @WithMockUser(roles = ["ADMIN"])
    fun `should be able to create a category when user is admin and return status code 201`() {
        every { categoryService.save(any()) } returns category
        mockMvc.perform(post("/categories")
            .contentType(MediaType.APPLICATION_JSON)
            .content(dtoAsString))
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.id").value(category.id))
            .andExpect(jsonPath("$.name").value(category.name))
            .andExpect(jsonPath("$.createdAt").isNotEmpty)
            .andExpect(jsonPath("$.updatedAt").isNotEmpty)
    }

    @Test
    @WithMockUser(roles = ["EMPLOYEE"])
    fun `should be able to create a category when user is employee and return status code 201`() {
        every { categoryService.save(any()) } returns category
        mockMvc.perform(post("/categories")
            .contentType(MediaType.APPLICATION_JSON)
            .content(dtoAsString))
            .andExpect(status().isCreated)
    }

    @Test
    @WithMockUser(roles = ["CUSTOMER"])
    fun `should not be able to create a category when user is customer and return status code 403`() {
        mockMvc.perform(post("/categories")
            .contentType(MediaType.APPLICATION_JSON)
            .content(dtoAsString))
            .andExpect(status().isForbidden)
    }

    @Test
    @WithMockUser(roles = ["ADMIN"])
    fun `should thrown CategoryAlreadyExistsException when category already exists and return status code 409`() {
        val message = "category already exists"
        every { categoryService.save(any()) } throws CategoryAlreadyExistsException(message)
        val result = mockMvc.perform(post("/categories")
            .contentType(MediaType.APPLICATION_JSON)
            .content(dtoAsString))
            .andExpect(status().isConflict)
            .andReturn()
        assertThat(result.response.contentAsString).isEqualTo(message)
    }

    // findCategoryById
    @Test
    @WithMockUser(roles = ["ADMIN"])
    fun `should be able to access data when user is admin and return status code 200`() {
        every { categoryService.findById(any()) } returns category
        mockMvc.perform(get("/categories/1"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(category.id))
            .andExpect(jsonPath("$.name").value(category.name))
            .andExpect(jsonPath("$.createdAt").isNotEmpty)
            .andExpect(jsonPath("$.updatedAt").isNotEmpty)
    }

    @Test
    @WithMockUser(roles = ["EMPLOYEE"])
    fun `should be able to access data when user is employee and return status code 200`() {
        every { categoryService.findById(any()) } returns category
        mockMvc.perform(get("/categories/1"))
            .andExpect(status().isOk)
    }

    @Test
    @WithMockUser(roles = ["CUSTOMER"])
    fun `should not be able to access data when user is customer and return status code 403`() {
        mockMvc.perform(get("/categories/1"))
            .andExpect(status().isForbidden)
    }

    @Test
    @WithMockUser(roles = ["ADMIN"])
    fun `should thrown CategoryNotFoundException when category does not exists and return status code 404`() {
        val message = "category already exists"
        every { categoryService.findById(any()) } throws CategoryNotFoundException(message)
        val result = mockMvc.perform(get("/categories/1"))
            .andExpect(status().isNotFound)
            .andReturn()
        assertThat(result.response.contentAsString).isEqualTo(message)
    }

    // findAllCategories
    @Test
    @WithMockUser(roles = ["ADMIN"])
    fun `should be able to access all data when user is admin and return status code 200`() {
        every { categoryService.findAll(any()) } returns Page.empty()
        mockMvc.perform(get("/categories"))
            .andExpect(status().isOk)
    }

    @Test
    @WithMockUser(roles = ["EMPLOYEE"])
    fun `should be able to access all data when user is employee and return status code 200`() {
        every { categoryService.findAll(any()) } returns Page.empty()
        mockMvc.perform(get("/categories"))
            .andExpect(status().isOk)
    }

    @Test
    @WithMockUser(roles = ["CUSTOMER"])
    fun `should not be able to access all data when user is customer and return status code 403`() {
        mockMvc.perform(get("/categories"))
            .andExpect(status().isForbidden)
    }

    // update category
    @Test
    @WithMockUser(roles = ["ADMIN"])
    fun `should be able to update a category when user is admin and return status code 200`() {
        every { categoryService.update(any(), any()) } returns category
        mockMvc.perform(put("/categories/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(dtoAsString))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(category.id))
            .andExpect(jsonPath("$.name").value(category.name))
            .andExpect(jsonPath("$.createdAt").isNotEmpty)
            .andExpect(jsonPath("$.updatedAt").isNotEmpty)
    }

    @Test
    @WithMockUser(roles = ["EMPLOYEE"])
    fun `should be able to update a category when user is employee and return status code 200`() {
        every { categoryService.update(any(), any()) } returns category
        mockMvc.perform(put("/categories/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(dtoAsString))
            .andExpect(status().isOk)
    }

    @Test
    @WithMockUser(roles = ["CUSTOMER"])
    fun `should be able to update a category when user is customer and return status code 403`() {
        mockMvc.perform(put("/categories/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(dtoAsString))
            .andExpect(status().isForbidden)
    }

    @Test
    @WithMockUser(roles = ["ADMIN"])
    fun `should not be able update a non-existing category and return status code 404`() {
        val message = "category not found"
        every { categoryService.update(any(), any()) } throws CategoryNotFoundException(message)
        val result = mockMvc.perform(put("/categories/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(dtoAsString))
            .andExpect(status().isNotFound)
            .andReturn()
        assertThat(result.response.contentAsString).isEqualTo(message)
    }

    @Test
    @WithMockUser(roles = ["ADMIN"])
    fun `should not be able update a category when category name already exists and return status code 409`() {
        val message = "category not found"
        every { categoryService.update(any(), any()) } throws CategoryAlreadyExistsException(message)
        val result = mockMvc.perform(put("/categories/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(dtoAsString))
            .andExpect(status().isConflict)
            .andReturn()
        assertThat(result.response.contentAsString).isEqualTo(message)
    }

    // delete
    @Test
    @WithMockUser(roles = ["ADMIN"])
    fun `should not be able delete a non-existing category and return status code 404`() {
        val message = "category not found"
        every { categoryService.delete(any()) } throws CategoryNotFoundException(message)
        val result = mockMvc.perform(delete("/categories/1"))
            .andExpect(status().isNotFound)
            .andReturn()
        assertThat(result.response.contentAsString).isEqualTo(message)
    }

    @Test
    @WithMockUser(roles = ["ADMIN"])
    fun `should be able to delete a category when user is admin and return status code 204`() {
        every { categoryService.delete(any()) } returns Unit
        mockMvc.perform(delete("/categories/1"))
            .andExpect(status().isNoContent)
    }

    @Test
    @WithMockUser(roles = ["EMPLOYEE"])
    fun `should be able to delete a category when user is employee and return status code 204`() {
        every { categoryService.delete(any()) } returns Unit
        mockMvc.perform(delete("/categories/1"))
            .andExpect(status().isNoContent)
    }

    @Test
    @WithMockUser(roles = ["CUSTOMER"])
    fun `should not be able to delete a category when user is customer and return status code 403`() {
        mockMvc.perform(delete("/categories/1"))
            .andExpect(status().isForbidden)
    }


}