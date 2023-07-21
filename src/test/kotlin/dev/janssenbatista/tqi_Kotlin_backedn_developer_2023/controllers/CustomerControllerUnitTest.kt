package dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.buildUser
import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.buildUserRequestDto
import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.dtos.UserRequestDto
import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.enums.Role
import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.exceptions.CustomerAlreadyExistsException
import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.exceptions.CustomerNotFoundException
import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.exceptions.ForbiddenException
import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.security.JwtAuthFilter
import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.security.JwtService
import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.security.WebSecurity
import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.services.CustomerService
import io.mockk.every
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.*

@WebMvcTest(CustomerController::class)
@Import(WebSecurity::class, JwtAuthFilter::class, JwtService::class)
class CustomerControllerUnitTest {

    @Autowired
    private lateinit var mapper: ObjectMapper

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockkBean
    private lateinit var customerService: CustomerService

    private lateinit var userRequestDto: UserRequestDto
    private lateinit var dtoAsString: String

    @BeforeEach
    fun setup() {
        userRequestDto = buildUserRequestDto()
        dtoAsString = mapper.writeValueAsString(userRequestDto)
    }

    @Test
    fun `should be able to create a customer and return status code 201`() {
        val customer = buildUser(roleId = Role.CUSTOMER.value)
        every { customerService.save(userRequestDto) } returns customer
        mockMvc.perform(post("/customers")
            .contentType(MediaType.APPLICATION_JSON)
            .content(dtoAsString))
            .andExpect(status().isCreated)
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(customer.id.toString()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(customer.name))
            .andExpect(MockMvcResultMatchers.jsonPath("$.email").value(customer.email))
            .andExpect(MockMvcResultMatchers.jsonPath("$.createdAt").isNotEmpty)
            .andExpect(MockMvcResultMatchers.jsonPath("$.updatedAt").isNotEmpty)
    }

    @Test
    fun `should not be able to create a customer with existing email and return status code 409`() {
        val message = "customer already exists"
        every { customerService.save(any()) } throws CustomerAlreadyExistsException(message)
        val result = mockMvc.perform(post("/customers")
            .contentType(MediaType.APPLICATION_JSON)
            .content(dtoAsString))
            .andExpect(status().isConflict)
            .andReturn()
        assertThat(result.response.contentAsString).isEqualTo(message)
    }

    @Test
    @WithMockUser(roles = ["ADMIN"])
    fun `should be able to access data of a customer when user is admin and return status code 200`() {
        val customer = buildUser(roleId = Role.CUSTOMER.value)
        every { customerService.findById(customer.id!!) } returns customer
        mockMvc.perform(get("/customers/${customer.id}/profile"))
            .andExpect(status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(customer.id.toString()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(customer.name))
            .andExpect(MockMvcResultMatchers.jsonPath("$.email").value(customer.email))
            .andExpect(MockMvcResultMatchers.jsonPath("$.createdAt").isNotEmpty)
            .andExpect(MockMvcResultMatchers.jsonPath("$.updatedAt").isNotEmpty)
    }

    @Test
    @WithMockUser(roles = ["EMPLOYEE"])
    fun `should be able to access data of a customer when user is employee and return status code 200`() {
        val customer = buildUser(roleId = Role.CUSTOMER.value)
        every { customerService.findById(customer.id!!) } returns customer
        mockMvc.perform(get("/customers/${customer.id}/profile"))
            .andExpect(status().isOk)
    }

    @Test
    @WithMockUser(roles = ["CUSTOMER"])
    fun `should be able to access data of an customer when user is owner of data and return status code 200`() {
        val customer = buildUser(roleId = Role.CUSTOMER.value)
        every { customerService.findById(customer.id!!) } returns customer
        mockMvc.perform(get("/customers/${customer.id}/profile"))
            .andExpect(status().isOk)
    }

    @Test
    @WithMockUser(roles = ["CUSTOMER"])
    fun `should not be able to access data of a customer when user is not the owner of data and return status code 403`() {
        val message = "only admin or the owner can see this information"
        every {
            customerService.findById(any())
        } throws ForbiddenException(message)
        val result = mockMvc.perform(get("/customers/${UUID.randomUUID()}/profile"))
            .andExpect(status().isForbidden)
            .andReturn()
        assertThat(result.response.contentAsString).isEqualTo(message)
    }

    @Test
    @WithMockUser(roles = ["ADMIN"])
    fun `should not be able to access data of non-existent customer and return status code 404`() {
        val message = "customer not found"
        every { customerService.findById(any()) } throws CustomerNotFoundException(message)
        val result = mockMvc.perform(get("/customers/${UUID.randomUUID()}/profile"))
            .andExpect(status().isNotFound)
            .andReturn()
        assertThat(result.response.contentAsString).isEqualTo(message)
    }


    @Test
    @WithMockUser(roles = ["ADMIN"])
    fun `should be able to update a customer when user is admin and return status code 200`() {
        val updatedCustomer = buildUser(roleId = Role.CUSTOMER.value)
        every { customerService.update(any(), userRequestDto) } returns updatedCustomer
        mockMvc.perform(put("/customers/${updatedCustomer.id!!}")
            .contentType(MediaType.APPLICATION_JSON)
            .content(dtoAsString)
        ).andExpect(status().isOk)
    }

    @Test
    @WithMockUser(roles = ["EMPLOYEE"])
    fun `should not be able to update a customer when user is employee and return status code 403`() {
        mockMvc.perform(put("/customers/${UUID.randomUUID()}"))
            .andExpect(status().isForbidden)
    }

    @Test
    @WithMockUser(roles = ["CUSTOMER"])
    fun `should be able to update own data and return status code 200`() {
        val updatedCustomer = buildUser(roleId = Role.CUSTOMER.value)
        every { customerService.update(any(), userRequestDto) } returns updatedCustomer
        mockMvc.perform(put("/customers/${updatedCustomer.id!!}")
            .contentType(MediaType.APPLICATION_JSON)
            .content(dtoAsString)
        ).andExpect(status().isOk)
    }

    @Test
    @WithMockUser(roles = ["ADMIN"])
    fun `should not be able to update data of non-existent customer and return status code 404`() {
        val message = "customer not found"
        every { customerService.update(any(), userRequestDto) } throws CustomerNotFoundException(message)
        val result = mockMvc.perform(put("/customers/${UUID.randomUUID()}")
            .contentType(MediaType.APPLICATION_JSON)
            .content(dtoAsString))
            .andExpect(status().isNotFound)
            .andReturn()
        assertThat(result.response.contentAsString).isEqualTo(message)
    }

    @Test
    @WithMockUser(roles = ["CUSTOMER"])
    fun `should not be able to update data of other customer and return status code 403`() {
        val message = "you cannot update data of other customer"
        every { customerService.update(any(), userRequestDto) } throws ForbiddenException(message)
        val result = mockMvc.perform(put("/customers/${UUID.randomUUID()}")
            .contentType(MediaType.APPLICATION_JSON)
            .content(dtoAsString))
            .andExpect(status().isForbidden)
            .andReturn()
        assertThat(result.response.contentAsString).isEqualTo(message)
    }

    @Test
    @WithMockUser(roles = ["ADMIN"])
    fun `should be able to delete a customer when user is admin and return status code 204`() {
        every { customerService.delete(any()) } returns Unit
        mockMvc.perform(delete("/customers/${UUID.randomUUID()}"))
            .andExpect(status().isNoContent)
    }

    @Test
    @WithMockUser(roles = ["EMPLOYEE"])
    fun `should not be able to delete a customer when user is employee and return status code 403`() {
        every { customerService.delete(any()) } returns Unit
        mockMvc.perform(delete("/customers/${UUID.randomUUID()}"))
            .andExpect(status().isForbidden)
    }

    @Test
    @WithMockUser(roles = ["CUSTOMER"])
    fun `should be able to delete own data and return status code 204`() {
        every { customerService.delete(any()) } returns Unit
        mockMvc.perform(delete("/customers/${UUID.randomUUID()}"))
            .andExpect(status().isNoContent)
    }

    @Test
    @WithMockUser(roles = ["CUSTOMER"])
    fun `should not be able to delete data of other customer and return status code 403`() {
        val message = "you cannot delete data of other customer"
        every { customerService.delete(any()) } throws ForbiddenException(message)
        mockMvc.perform(delete("/customers/${UUID.randomUUID()}"))
            .andExpect(status().isForbidden)
    }

    @Test
    @WithMockUser(roles = ["ADMIN"])
    fun `should not be able to delete data of non-existent customer and return status code 404`() {
        val message = "customer not found"
        every { customerService.update(any(), userRequestDto) } throws CustomerNotFoundException(message)
        val result = mockMvc.perform(put("/customers/${UUID.randomUUID()}")
            .contentType(MediaType.APPLICATION_JSON)
            .content(dtoAsString))
            .andExpect(status().isNotFound)
            .andReturn()
        assertThat(result.response.contentAsString).isEqualTo(message)
    }

}