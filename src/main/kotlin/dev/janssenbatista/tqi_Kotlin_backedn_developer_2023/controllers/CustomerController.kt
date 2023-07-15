package dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.controllers

import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.dtos.UserRequestDto
import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.dtos.UserResponseDto
import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.services.CustomerService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/customers")
class CustomerController(private val customerService: CustomerService) {

    @PostMapping
    fun createCustomer(@RequestBody @Valid userRequestDto: UserRequestDto): ResponseEntity<UserResponseDto> {
        val createdCustomer = customerService.save(userRequestDto)
        return ResponseEntity.status(HttpStatus.CREATED).body(UserResponseDto.fromEntity(createdCustomer))
    }

    @GetMapping("/{customerId}/profile")
    fun getProfile(@PathVariable customerId: UUID): ResponseEntity<UserResponseDto> {
        val foundCustomer = customerService.findById(customerId)
        return ResponseEntity.ok(UserResponseDto.fromEntity(foundCustomer))
    }

    @PutMapping("/{customerId}")
    fun updateEmployee(@PathVariable customerId: UUID,
                       @RequestBody @Valid userRequestDto: UserRequestDto): ResponseEntity<UserResponseDto> {
        val updatedCustomer = customerService.update(customerId, userRequestDto)
        return ResponseEntity.ok(UserResponseDto.fromEntity(updatedCustomer))
    }

    @DeleteMapping("/{customerId}")
    fun deleteEmployee(@PathVariable customerId: UUID): ResponseEntity<Any> {
        customerService.delete(customerId)
        return ResponseEntity.noContent().build()
    }

}