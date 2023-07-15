package dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.services

import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.buildUser
import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.buildUserRequestDto
import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.dtos.UserRequestDto
import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.enums.Role
import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.exceptions.CustomerAlreadyExistsException
import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.exceptions.CustomerNotFoundException
import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.exceptions.EmployeeAlreadyExistsException
import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.models.User
import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.repositories.UserRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.security.core.context.SecurityContextHolder
import java.time.ZonedDateTime
import java.util.*


class CustomerServiceUnitTest {


    private val userRepository = mockk<UserRepository>()
    private val customerService: CustomerService = CustomerService(userRepository)

    private val userRequestDto: UserRequestDto = buildUserRequestDto()
    private lateinit var customer: User

    @BeforeEach
    fun setup() {
        customer = buildUser(roleId = Role.CUSTOMER.value)
    }

    @Test
    fun `should be able to save a customer`() {
        every { userRepository.findByEmail(any()) } returns Optional.empty()
        every { userRepository.save(any()) } returns customer
        val createdCustomer = customerService.save(userRequestDto)
        assertThat(createdCustomer.name).isEqualTo(customer.name)
        assertThat(createdCustomer.email).isEqualTo(customer.email)
        assertThat(createdCustomer.password).isNotEqualTo(userRequestDto.password)
        verify(exactly = 1) { userRepository.findByEmail(any()) }
        verify(exactly = 1) { userRepository.save(any()) }
    }

    @Test
    fun `should not be able to save a customer with existing email`() {
        every { userRepository.findByEmail(userRequestDto.email) } returns Optional.of(customer)
        assertThatExceptionOfType(CustomerAlreadyExistsException::class.java).isThrownBy {
            customerService.save(userRequestDto)
        }.withMessage("customer already exists")
        verify(exactly = 1) { userRepository.findByEmail(any()) }
        verify(exactly = 0) { userRepository.save(any()) }
    }

    @Test
    fun `should be able to find a customer by id`() {
        every { userRepository.findById(any()) } returns Optional.of(customer)
        val foundCustomer = customerService.findById(customer.id!!)
        assertThat(foundCustomer).isNotNull
        verify(exactly = 1) { userRepository.findById(any()) }
    }

    @Test
    fun `should not be able to find a customer by id`() {
        every { userRepository.findById(any()) } returns Optional.empty()
        assertThatExceptionOfType(CustomerNotFoundException::class.java).isThrownBy {
            customerService.findById(customer.id!!)
        }.withMessage("customer not found")
        verify(exactly = 1) { userRepository.findById(any()) }
    }

    @Test
    fun `should not be able to update a customer when id is invalid`() {
        val customerId = UUID.randomUUID()
        every { userRepository.findById(any()) } returns Optional.empty()
        assertThatExceptionOfType(CustomerNotFoundException::class.java).isThrownBy {
            customerService.update(customerId, userRequestDto)
        }.withMessage("customer not found")
        verify(exactly = 1) { userRepository.findById(customerId) }
        verify(exactly = 0) { userRepository.findByEmail(any()) }
        verify(exactly = 0) { userRepository.save(any()) }
    }

    @Test
    fun `should not be able to update a customer when email already exists`() {
        val customerToUpdate = customer
        val otherCustomer = buildUser(name = "other customer", roleId = Role.CUSTOMER.value)
        every { userRepository.findById(customer.id!!) } returns Optional.of(customerToUpdate)
        every { userRepository.findByEmail(customer.email) } returns Optional.of(otherCustomer)
        assertThatExceptionOfType(CustomerAlreadyExistsException::class.java).isThrownBy {
            customerService.update(customer.id!!, userRequestDto)
        }.withMessage("customer with the same email already exists")
        verify(exactly = 1) { userRepository.findById(customer.id!!) }
        verify(exactly = 1) { userRepository.findByEmail(any()) }
        verify(exactly = 0) { userRepository.save(any()) }
    }

    @Test
    fun `should be able to update a customer`() {
        val userId = customer.id!!
        every { userRepository.findById(userId) } returns Optional.of(customer)
        every { userRepository.findByEmail(any()) } returns Optional.empty()
        val dataToUpdate = buildUserRequestDto(
                name = "updated name",
                email = "updatedUser@email.com",
                password = "22222222"
        )
        val updatedCustomer = buildUser(
                id = customer.id!!,
                name = dataToUpdate.name,
                email = dataToUpdate.email,
                password = "otherEncryptedHashPassword",
                roleId = customer.roleId,
                isEnabled = customer.isEnabled,
                createdAt = customer.createdAt,
                updatedAt = ZonedDateTime.now()
        )
        every { userRepository.save(any()) } returns updatedCustomer
        customerService.update(userId, dataToUpdate)
        assertThat(customer.id!!).isEqualTo(updatedCustomer.id)
        assertThat(customer.name).isEqualTo(updatedCustomer.name)
        assertThat(customer.email).isEqualTo(updatedCustomer.email)
        assertThat(customer.password).isNotEqualTo(updatedCustomer.password)
        assertThat(customer.roleId).isEqualTo(updatedCustomer.roleId)
        assertThat(customer.isEnabled).isEqualTo(true)
        assertThat(customer.createdAt).isEqualTo(updatedCustomer.createdAt)
        assertThat(customer.updatedAt).isNotEqualTo(updatedCustomer.updatedAt)
        verify(exactly = 1) { userRepository.findById(userId) }
        verify(exactly = 1) { userRepository.findByEmail(customer.email) }
        verify(exactly = 1) { userRepository.save(any()) }
    }

    @Test
    fun `should not be able to delete a customer when id is invalid`() {
        val customerId = UUID.randomUUID()
        every { userRepository.findById(any()) } returns Optional.empty()
        assertThatExceptionOfType(CustomerNotFoundException::class.java).isThrownBy {
            customerService.delete(customerId)
        }.withMessage("customer not found")
        verify(exactly = 1) { userRepository.findById(customerId) }
        verify(exactly = 0) { userRepository.save(any()) }
    }

    @Test
    fun `should be able to delete a customer`() {
        every { userRepository.findById(customer.id!!) } returns Optional.of(customer)
        every { userRepository.save(customer) } returns customer
        customerService.delete(customer.id!!)
        assertThat(customer.isEnabled).isEqualTo(false)
        verify(exactly = 1) { userRepository.findById(customer.id!!) }
        verify(exactly = 1) { userRepository.save(customer) }
    }


}