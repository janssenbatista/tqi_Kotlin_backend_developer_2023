package dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.services

import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.dtos.UserRequestDto
import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.enums.Role
import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.exceptions.EmployeeAlreadyExistsException
import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.exceptions.EmployeeNotFoundException
import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.exceptions.ForbiddenException
import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.models.User
import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.repositories.UserRepository
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import java.time.ZonedDateTime
import java.util.*

@Service
class EmployeeService(private val userRepository: UserRepository) {

    fun save(userRequestDto: UserRequestDto): User {
        val employeeAlreadyExists = userRepository.findByEmail(userRequestDto.email)
        if (employeeAlreadyExists.isPresent) {
            throw EmployeeAlreadyExistsException("employee already exists")
        }
        val user = userRequestDto.toEntity(Role.EMPLOYEE)
        return userRepository.save(user)
    }

    fun findById(userId: UUID): User {
        verifyPermission(userId = userId, message = "Only admin or the owner can see this information")
        val employee = userRepository.findById(userId).orElseThrow {
            throw EmployeeNotFoundException("employee not found")
        }
        return employee
    }

    fun update(userId: UUID, userRequestDto: UserRequestDto): User {
        val employeeToUpdate = userRepository.findById(userId).orElseThrow {
            throw EmployeeNotFoundException("employee not found")
        }
        employeeToUpdate.apply {
            name = userRequestDto.name
            email = userRequestDto.email
            password = BCryptPasswordEncoder(12).encode(userRequestDto.password)
            updatedAt = ZonedDateTime.now()
        }
        val otherEmployeeWithSameEmail = userRepository.findByEmail(userRequestDto.email)
        if (otherEmployeeWithSameEmail.isPresent && employeeToUpdate != otherEmployeeWithSameEmail.get()) {
            throw EmployeeAlreadyExistsException("employee with the same email already exists")
        }
        return userRepository.save(employeeToUpdate)
    }

    fun delete(userId: UUID) {
        val employee = userRepository.findById(userId).orElseThrow {
            throw EmployeeNotFoundException("employee not found")
        }
        employee.isEnabled = false
        userRepository.save(employee)
    }

    private fun verifyPermission(userId: UUID, message: String?) {
        if (SecurityContextHolder.getContext().authentication != null) {
            val email = SecurityContextHolder.getContext().authentication.name
            val user = userRepository.findByEmail(email).orElseThrow { throw IllegalArgumentException() }
            if (user.roleId != -1 && user.id != userId) {
                throw ForbiddenException(message)
            }
        }
    }
}