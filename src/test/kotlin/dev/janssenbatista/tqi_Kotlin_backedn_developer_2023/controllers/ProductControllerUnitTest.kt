package dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.buildProduct
import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.buildProductRequestDto
import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.dtos.ProductRequestDto
import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.exceptions.ProductAlreadyExistsException
import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.exceptions.ProductNotFoundException
import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.models.Product
import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.security.WebSecurity
import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.services.ProductService
import io.mockk.every
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithAnonymousUser
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.UUID

@WebMvcTest(ProductController::class)
@Import(WebSecurity::class)
class ProductControllerUnitTest {

    @MockkBean
    private lateinit var productService: ProductService

    @Autowired
    private lateinit var mapper: ObjectMapper

    @Autowired
    private lateinit var mockMvc: MockMvc

    private lateinit var dto: ProductRequestDto
    private lateinit var dtoAsString: String
    private lateinit var product: Product

    @BeforeEach
    fun setup() {
        dto = buildProductRequestDto()
        dtoAsString = mapper.writeValueAsString(dto)
        product = buildProduct()
    }

    // createProduct
    @Test
    @WithMockUser(roles = ["EMPLOYEE"])
    fun `should be able to create a product when user is admin and return status code 201`() {
        every { productService.save(dto) } returns product
        mockMvc.perform(post("/products")
            .contentType(MediaType.APPLICATION_JSON)
            .content(dtoAsString))
            .andExpect(status().isCreated)
    }

    @Test
    @WithMockUser(roles = ["EMPLOYEE"])
    fun `should be able to create a product when product already exists and return status code 409`() {
        val message = "product already exists"
        every { productService.save(dto) } throws ProductAlreadyExistsException(message)
        val result = mockMvc.perform(post("/products")
            .contentType(MediaType.APPLICATION_JSON)
            .content(dtoAsString))
            .andExpect(status().isConflict)
            .andReturn()
        Assertions.assertThat(result.response.contentAsString).isEqualTo(message)
    }

    @Test
    @WithMockUser(roles = ["ADMIN"])
    fun `should not be able to create a product when user is admin and return status code 403`() {
        mockMvc.perform(post("/products")
            .contentType(MediaType.APPLICATION_JSON)
            .content(dtoAsString))
            .andExpect(status().isForbidden)
    }

    @Test
    @WithMockUser(roles = ["CUSTOMER"])
    fun `should not be able to create a product when user is customer and return status code 403`() {
        mockMvc.perform(post("/products")
            .contentType(MediaType.APPLICATION_JSON)
            .content(dtoAsString))
            .andExpect(status().isForbidden)
    }

    // findAllProducts and findProductById
    @Test
    @WithAnonymousUser
    fun `should not be able to access products info when user is not authenticated and return status code 401`() {
        mockMvc.perform(get("/products"))
            .andExpect(status().isUnauthorized)
        mockMvc.perform(get("/products/${UUID.randomUUID()}"))
            .andExpect(status().isUnauthorized)
    }

    @Test
    @WithMockUser(roles = ["ADMIN"])
    fun `should not be able to access products info when id is not valid and return status code 404`() {
        every { productService.findById(any()) } returns product
        mockMvc.perform(get("/products/${UUID.randomUUID()}"))
            .andExpect(status().isOk)
    }

    // updateProduct
    @Test
    @WithMockUser(roles = ["EMPLOYEE"])
    fun `should be able to update a product when user is employee and return status code 200`() {
        every { productService.update(any(), dto) } returns product
        mockMvc.perform(put("/products/${UUID.randomUUID()}")
            .contentType(MediaType.APPLICATION_JSON)
            .content(dtoAsString))
            .andExpect(status().isOk)
    }

    @Test
    @WithMockUser(roles = ["EMPLOYEE"])
    fun `should not be able to update a product when product does not exists and return status code 404`() {
        val message = "product not found"
        every { productService.update(any(), dto) } throws ProductNotFoundException(message)
        val result = mockMvc.perform(put("/products/${UUID.randomUUID()}")
            .contentType(MediaType.APPLICATION_JSON)
            .content(dtoAsString))
            .andExpect(status().isNotFound)
            .andReturn()
        assertThat(result.response.contentAsString).isEqualTo(message)
    }

    @Test
    @WithMockUser(roles = ["EMPLOYEE"])
    fun `should not be able to update a product when other product with the same name already exists and return status code 404`() {
        val message = "other product with the same name already exists"
        every { productService.update(any(), dto) } throws ProductAlreadyExistsException(message)
        val result = mockMvc.perform(put("/products/${UUID.randomUUID()}")
            .contentType(MediaType.APPLICATION_JSON)
            .content(dtoAsString))
            .andExpect(status().isConflict)
            .andReturn()
        assertThat(result.response.contentAsString).isEqualTo(message)
    }

    @Test
    @WithMockUser(roles = ["ADMIN"])
    fun `should not be able to update a product when user is admin and return status code 403`() {
        mockMvc.perform(put("/products/${UUID.randomUUID()}")
            .contentType(MediaType.APPLICATION_JSON)
            .content(dtoAsString))
            .andExpect(status().isForbidden)
    }

    @Test
    @WithMockUser(roles = ["CUSTOMER"])
    fun `should not be able to update a product when user is customer and return status code 403`() {
        mockMvc.perform(put("/products/${UUID.randomUUID()}")
            .contentType(MediaType.APPLICATION_JSON)
            .content(dtoAsString))
            .andExpect(status().isForbidden)
    }

    // deleteProduct
    @Test
    @WithMockUser(roles = ["EMPLOYEE"])
    fun `should be able to delete a product when user is employee and return status code 204`() {
        every { productService.delete(any()) } returns Unit
        mockMvc.perform(delete("/products/${UUID.randomUUID()}"))
            .andExpect(status().isNoContent)
    }

    @Test
    @WithMockUser(roles = ["EMPLOYEE"])
    fun `should not be able to delele a product when product does not exists and return status code 404`() {
        val message = "product not found"
        every { productService.delete(any()) } throws ProductNotFoundException(message)
        val result = mockMvc.perform(delete("/products/${UUID.randomUUID()}"))
            .andExpect(status().isNotFound)
            .andReturn()
        assertThat(result.response.contentAsString).isEqualTo(message)
    }

}