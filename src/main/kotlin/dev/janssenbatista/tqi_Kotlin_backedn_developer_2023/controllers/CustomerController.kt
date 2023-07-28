package dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.controllers

import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.dtos.UserRequestDto
import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.dtos.UserResponseDto
import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.services.CustomerService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/customers")
@Tag(name = "Customer")
class CustomerController(private val customerService: CustomerService) {

    @Operation(
        summary = "create a customer",
        responses = [
            ApiResponse(description = "customer already exists", responseCode = "409"),
            ApiResponse(description = "customer created", responseCode = "201")
        ]
    )
    @PostMapping
    fun createCustomer(@RequestBody @Valid userRequestDto: UserRequestDto): ResponseEntity<UserResponseDto> {
        val createdCustomer = customerService.save(userRequestDto)
        return ResponseEntity.status(HttpStatus.CREATED).body(UserResponseDto.fromEntity(createdCustomer))
    }


    @Operation(
        summary = "get customer info",
        responses = [
            ApiResponse(description = "customer info", responseCode = "200"),
            ApiResponse(description = "customer not found", responseCode = "404"),
            ApiResponse(description = "only admin or the owner can see this information", responseCode = "403"),
        ]
    )
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/{customerId}/profile")
    fun getProfile(@PathVariable customerId: UUID): ResponseEntity<UserResponseDto> {
        val foundCustomer = customerService.findById(customerId)
        return ResponseEntity.ok(UserResponseDto.fromEntity(foundCustomer))
    }

    @Operation(
        summary = "update a customer by id",
        responses = [
            ApiResponse(description = "customer updated", responseCode = "200"),
            ApiResponse(description = "customer not found", responseCode = "404"),
            ApiResponse(description = "only admin or the owner can update", responseCode = "403"),
            ApiResponse(description = "customer with the same email already exists", responseCode = "409"),
        ]
    )
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/{customerId}")
    fun updateEmployee(@PathVariable customerId: UUID,
                       @RequestBody @Valid userRequestDto: UserRequestDto): ResponseEntity<UserResponseDto> {
        val updatedCustomer = customerService.update(customerId, userRequestDto)
        return ResponseEntity.ok(UserResponseDto.fromEntity(updatedCustomer))
    }

    @Operation(
        summary = "delete a customer by id",
        responses = [
            ApiResponse(description = "customer deleted", responseCode = "204"),
            ApiResponse(description = "customer not found", responseCode = "404"),
            ApiResponse(description = "only owner can delete your data", responseCode = "403")
        ]
    )
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/{customerId}")
    fun deleteEmployee(@PathVariable customerId: UUID): ResponseEntity<Any> {
        customerService.delete(customerId)
        return ResponseEntity.noContent().build()
    }

}