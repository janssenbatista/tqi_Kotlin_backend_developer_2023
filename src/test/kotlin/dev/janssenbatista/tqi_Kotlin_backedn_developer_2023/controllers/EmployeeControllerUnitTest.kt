package dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.buildUser
import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.buildUserRequestDto
import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.dtos.UserRequestDto
import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.enums.Role
import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.exceptions.ForbiddenException
import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.security.JwtAuthFilter
import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.security.JwtService
import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.security.WebSecurity
import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.services.EmployeeService
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

@WebMvcTest(EmployeeController::class)
@Import(WebSecurity::class, JwtAuthFilter::class, JwtService::class)
class EmployeeControllerUnitTest {

    @Autowired
    private lateinit var mapper: ObjectMapper

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockkBean
    private lateinit var employeeService: EmployeeService

    private lateinit var userRequestDto: UserRequestDto
    private lateinit var dtoAsString: String

    @BeforeEach
    fun setup() {
        userRequestDto = buildUserRequestDto()
        dtoAsString = mapper.writeValueAsString(userRequestDto)
    }

    @Test
    @WithMockUser(roles = ["ADMIN"])
    fun `should be able to create a employee when user is admin and return status code 201`() {
        val employee = buildUser(roleId = Role.EMPLOYEE.value)
        every { employeeService.save(userRequestDto) } returns employee
        mockMvc.perform(post("/employees")
            .contentType(MediaType.APPLICATION_JSON)
            .content(dtoAsString))
            .andExpect(status().isCreated)
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(employee.id.toString()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(employee.name))
            .andExpect(MockMvcResultMatchers.jsonPath("$.email").value(employee.email))
            .andExpect(MockMvcResultMatchers.jsonPath("$.createdAt").isNotEmpty)
            .andExpect(MockMvcResultMatchers.jsonPath("$.updatedAt").isNotEmpty)
    }

    @Test
    @WithMockUser(roles = ["EMPLOYEE"])
    fun `should not be able to create a employee when user is employee and return status code 403`() {
        mockMvc.perform(post("/employees"))
            .andExpect(status().isForbidden)
    }

    @Test
    @WithMockUser(roles = ["CUSTOMER"])
    fun `should not be able to create a employee when user is customer and return status code 403`() {
        mockMvc.perform(post("/employees"))
            .andExpect(status().isForbidden)
    }

    @Test
    @WithMockUser(roles = ["ADMIN"])
    fun `should be able to access data of an employee when user is admin and return status code 200`() {
        val employee = buildUser(roleId = Role.EMPLOYEE.value)
        every { employeeService.findById(employee.id!!) } returns employee
        mockMvc.perform(get("/employees/${employee.id}/profile"))
            .andExpect(status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(employee.id.toString()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(employee.name))
            .andExpect(MockMvcResultMatchers.jsonPath("$.email").value(employee.email))
            .andExpect(MockMvcResultMatchers.jsonPath("$.createdAt").isNotEmpty)
            .andExpect(MockMvcResultMatchers.jsonPath("$.updatedAt").isNotEmpty)
    }

    @Test
    @WithMockUser(roles = ["EMPLOYEE"])
    fun `should be able to access data of an employee when user is owner of data and return status code 200`() {
        val employee = buildUser(roleId = Role.EMPLOYEE.value)
        every { employeeService.findById(employee.id!!) } returns employee
        mockMvc.perform(get("/employees/${employee.id}/profile"))
            .andExpect(status().isOk)
    }

    @Test
    @WithMockUser(roles = ["EMPLOYEE"])
    fun `should not be able to access data of an employee when user is not the owner of data and return status code 403`() {
        val message = "only admin or the owner can see this information"
        every {
            employeeService.findById(any())
        } throws ForbiddenException(message)
        val result = mockMvc.perform(get("/employees/${UUID.randomUUID()}/profile"))
            .andExpect(status().isForbidden)
            .andReturn()
        assertThat(result.response.contentAsString).isEqualTo(message)
    }

    @Test
    @WithMockUser(roles = ["ADMIN"])
    fun `should be able to update an employee when user is admin and return status code 200`() {
        val updateEmployee = buildUser(roleId = Role.EMPLOYEE.value)
        every { employeeService.update(any(), userRequestDto) } returns updateEmployee
        mockMvc.perform(put("/employees/${updateEmployee.id!!}")
            .contentType(MediaType.APPLICATION_JSON)
            .content(dtoAsString)
        ).andExpect(status().isOk)
    }

    @Test
    @WithMockUser(roles = ["EMPLOYEE"])
    fun `should be able to update an employee when user is employee and return status code 403`() {
        mockMvc.perform(put("/employees/${UUID.randomUUID()}")
        ).andExpect(status().isForbidden)
    }

    @Test
    @WithMockUser(roles = ["ADMIN"])
    fun `should be able to delete a employee when user is admin and return status code 204`() {
        every { employeeService.delete(any()) } returns Unit
        mockMvc.perform(delete("/employees/${UUID.randomUUID()}"))
            .andExpect(status().isNoContent)
    }

    @Test
    @WithMockUser(roles = ["EMPLOYEE"])
    fun `should not be able to delete a employee when user is employee and return status code 403`() {
        mockMvc.perform(delete("/employees/${UUID.randomUUID()}"))
            .andExpect(status().isForbidden)
    }

    @Test
    @WithMockUser(roles = ["CUSTOMER"])
    fun `should not be able to delete a employee when user is customer and return status code 403`() {
        mockMvc.perform(delete("/employees/${UUID.randomUUID()}"))
            .andExpect(status().isForbidden)
    }


}