package dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.services

import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.buildUser
import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.buildUserRequestDto
import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.dtos.UserRequestDto
import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.enums.Role
import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.exceptions.EmployeeAlreadyExistsException
import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.exceptions.EmployeeNotFoundException
import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.models.User
import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.repositories.UserRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.ZonedDateTime
import java.util.*


class EmployeeServiceUnitTest {


    private val userRepository = mockk<UserRepository>()
    private val employeeService: EmployeeService = EmployeeService(userRepository)

    private val userRequestDto: UserRequestDto = buildUserRequestDto()
    private lateinit var employee: User

    @BeforeEach
    fun setup() {
        employee = buildUser(roleId = Role.EMPLOYEE.value)
    }

    @Test
    fun `should be able to save a employee`() {
        every { userRepository.findByEmail(any()) } returns Optional.empty()
        every { userRepository.save(any()) } returns employee
        val createdEmployee = employeeService.save(userRequestDto)
        assertThat(createdEmployee.name).isEqualTo(employee.name)
        assertThat(createdEmployee.email).isEqualTo(employee.email)
        assertThat(createdEmployee.password).isNotEqualTo(userRequestDto.password)
        verify(exactly = 1) { userRepository.findByEmail(any()) }
        verify(exactly = 1) { userRepository.save(any()) }
    }

    @Test
    fun `should not be able to save a employee with existing email`() {
        every { userRepository.findByEmail(userRequestDto.email) } returns Optional.of(employee)
        assertThatExceptionOfType(EmployeeAlreadyExistsException::class.java).isThrownBy {
            employeeService.save(userRequestDto)
        }.withMessage("employee already exists")
        verify(exactly = 1) { userRepository.findByEmail(any()) }
        verify(exactly = 0) { userRepository.save(any()) }
    }

    @Test
    fun `should be able to find a employee by id`() {
        every { userRepository.findById(any()) } returns Optional.of(employee)
        val foundEmployee = employeeService.findById(employee.id!!)
        assertThat(foundEmployee).isNotNull
        verify(exactly = 1) { userRepository.findById(any()) }
    }

    @Test
    fun `should not be able to find a employee by id`() {
        every { userRepository.findById(any()) } returns Optional.empty()
        assertThatExceptionOfType(EmployeeNotFoundException::class.java).isThrownBy {
            employeeService.findById(employee.id!!)
        }.withMessage("employee not found")
        verify(exactly = 1) { userRepository.findById(any()) }
    }

    @Test
    fun `should not be able to update a employee when id is invalid`() {
        val employeeId = UUID.randomUUID()
        every { userRepository.findById(any()) } returns Optional.empty()
        assertThatExceptionOfType(EmployeeNotFoundException::class.java).isThrownBy {
            employeeService.update(employeeId, userRequestDto)
        }.withMessage("employee not found")
        verify(exactly = 1) { userRepository.findById(employeeId) }
        verify(exactly = 0) { userRepository.findByEmail(any()) }
        verify(exactly = 0) { userRepository.save(any()) }
    }

    @Test
    fun `should not be able to update a employee when email already exists`() {
        val employeeToUpdate = employee
        val otherEmployee = buildUser(name = "other employee", roleId = Role.EMPLOYEE.value)
        every { userRepository.findById(employee.id!!) } returns Optional.of(employeeToUpdate)
        every { userRepository.findByEmail(employee.email) } returns Optional.of(otherEmployee)
        assertThatExceptionOfType(EmployeeAlreadyExistsException::class.java).isThrownBy {
            employeeService.update(employee.id!!, userRequestDto)
        }.withMessage("employee with the same email already exists")
        verify(exactly = 1) { userRepository.findById(employee.id!!) }
        verify(exactly = 1) { userRepository.findByEmail(any()) }
        verify(exactly = 0) { userRepository.save(any()) }
    }

    @Test
    fun `should be able to update a employee`() {
        val userId = employee.id!!
        every { userRepository.findById(userId) } returns Optional.of(employee)
        every { userRepository.findByEmail(any()) } returns Optional.empty()
        val dataToUpdate = buildUserRequestDto(
                name = "updated name",
                email = "updatedUser@email.com",
                password = "22222222"
        )
        val updatedEmployee = buildUser(
                id = employee.id!!,
                name = dataToUpdate.name,
                email = dataToUpdate.email,
                password = "otherEncryptedHashPassword",
                roleId = employee.roleId,
                isEnabled = employee.isEnabled,
                createdAt = employee.createdAt,
                updatedAt = ZonedDateTime.now()
        )
        every { userRepository.save(any()) } returns updatedEmployee
        employeeService.update(userId, dataToUpdate)
        assertThat(employee.id!!).isEqualTo(updatedEmployee.id)
        assertThat(employee.name).isEqualTo(updatedEmployee.name)
        assertThat(employee.email).isEqualTo(updatedEmployee.email)
        assertThat(employee.password).isNotEqualTo(updatedEmployee.password)
        assertThat(employee.roleId).isEqualTo(updatedEmployee.roleId)
        assertThat(employee.isEnabled).isEqualTo(true)
        assertThat(employee.createdAt).isEqualTo(updatedEmployee.createdAt)
        assertThat(employee.updatedAt).isNotEqualTo(updatedEmployee.updatedAt)
        verify(exactly = 1) { userRepository.findById(userId) }
        verify(exactly = 1) { userRepository.findByEmail(employee.email) }
        verify(exactly = 1) { userRepository.save(any()) }
    }

    @Test
    fun `should not be able to delete a employee when id is invalid`() {
        val employeeId = UUID.randomUUID()
        every { userRepository.findById(any()) } returns Optional.empty()
        assertThatExceptionOfType(EmployeeNotFoundException::class.java).isThrownBy {
            employeeService.delete(employeeId)
        }.withMessage("employee not found")
        verify(exactly = 1) { userRepository.findById(employeeId) }
        verify(exactly = 0) { userRepository.save(any()) }
    }

    @Test
    fun `should be able to delete a employee`() {
        every { userRepository.findById(employee.id!!) } returns Optional.of(employee)
        every { userRepository.save(employee) } returns employee
        employeeService.delete(employee.id!!)
        assertThat(employee.isEnabled).isEqualTo(false)
        verify(exactly = 1) { userRepository.findById(employee.id!!) }
        verify(exactly = 1) { userRepository.save(employee) }
    }


}