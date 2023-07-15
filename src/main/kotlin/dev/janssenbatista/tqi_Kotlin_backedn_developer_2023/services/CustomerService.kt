package dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.services

import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.dtos.UserRequestDto
import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.enums.Role
import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.exceptions.CustomerAlreadyExistsException
import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.exceptions.CustomerNotFoundException
import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.exceptions.ForbiddenException
import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.models.User
import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.repositories.UserRepository
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import java.time.ZonedDateTime
import java.util.*

@Service
class CustomerService(private val userRepository: UserRepository) {

    fun save(userRequestDto: UserRequestDto): User {
        val customerAlreadyExists = userRepository.findByEmail(userRequestDto.email)
        if (customerAlreadyExists.isPresent) {
            throw CustomerAlreadyExistsException("customer already exists")
        }
        val user = userRequestDto.toEntity(Role.CUSTOMER)
        return userRepository.save(user)
    }

    fun findById(userId: UUID): User {
        verifyPermission(userId = userId,
                message = "Only admin or the owner can see this information")
        val customer = userRepository.findById(userId).orElseThrow {
            throw CustomerNotFoundException("customer not found")
        }
        return customer
    }

    fun update(userId: UUID, userRequestDto: UserRequestDto): User {
        val customerToUpdate = userRepository.findById(userId).orElseThrow {
            throw CustomerNotFoundException("customer not found")
        }
        customerToUpdate.apply {
            name = userRequestDto.name
            email = userRequestDto.email
            password = BCryptPasswordEncoder(12).encode(userRequestDto.password)
            updatedAt = ZonedDateTime.now()
        }
        val otherCustomerWithSameEmail = userRepository.findByEmail(userRequestDto.email)
        if (otherCustomerWithSameEmail.isPresent && customerToUpdate != otherCustomerWithSameEmail.get()) {
            throw CustomerAlreadyExistsException("customer with the same email already exists")
        }
        return userRepository.save(customerToUpdate)
    }

    fun delete(userId: UUID) {
        val customer = userRepository.findById(userId).orElseThrow {
            throw CustomerNotFoundException("customer not found")
        }
        customer.isEnabled = false
        userRepository.save(customer)
    }

    fun verifyPermission(userId: UUID, message: String?) {
        if (SecurityContextHolder.getContext().authentication != null) {
            val email = SecurityContextHolder.getContext().authentication.name
            val user = userRepository.findByEmail(email).orElseThrow { throw IllegalArgumentException() }
            if (user.roleId != -1 && user.id != userId) {
                throw ForbiddenException(message)
            }
        }
    }
}