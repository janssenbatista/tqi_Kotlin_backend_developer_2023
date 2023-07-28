package dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.controllers

import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.dtos.UserRequestDto
import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.dtos.UserResponseDto
import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.services.EmployeeService
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
@RequestMapping("/employees")
@Tag(name = "Employee")
@SecurityRequirement(name = "bearerAuth")
class EmployeeController(private val employeeService: EmployeeService) {

    @Operation(
        summary = "create a employee",
        responses = [
            ApiResponse(description = "employee already exists", responseCode = "409"),
            ApiResponse(description = "employee created", responseCode = "201"),
            ApiResponse(description = "only administrator can create a employee", responseCode = "403"),
        ]
    )
    @PostMapping
    fun createEmployee(@RequestBody @Valid userRequestDto: UserRequestDto): ResponseEntity<UserResponseDto> {
        val createdEmployee = employeeService.save(userRequestDto)
        return ResponseEntity.status(HttpStatus.CREATED).body(UserResponseDto.fromEntity(createdEmployee))
    }

    @Operation(
        summary = "get employee info",
        responses = [
            ApiResponse(description = "employee not found", responseCode = "404"),
            ApiResponse(description = "employee info", responseCode = "200"),
            ApiResponse(description = "only admin or the owner can see this information", responseCode = "403"),
        ]
    )
    @GetMapping("/{employeeId}/profile")
    fun getProfile(@PathVariable employeeId: UUID): ResponseEntity<UserResponseDto> {
        val foundEmployee = employeeService.findById(employeeId)
        return ResponseEntity.ok(UserResponseDto.fromEntity(foundEmployee))
    }

    @Operation(
        summary = "update employee by id",
        responses = [
            ApiResponse(description = "employee not found", responseCode = "404"),
            ApiResponse(description = "employee updated", responseCode = "200"),
            ApiResponse(description = "only admin or the owner can update this information", responseCode = "403"),
        ]
    )
    @PutMapping("/{employeeId}")
    fun updateEmployee(@PathVariable employeeId: UUID,
                       @RequestBody @Valid userRequestDto: UserRequestDto): ResponseEntity<UserResponseDto> {
        val updatedEmployee = employeeService.update(employeeId, userRequestDto)
        return ResponseEntity.ok(UserResponseDto.fromEntity(updatedEmployee))
    }

    @Operation(
        summary = "delete employee by id",
        responses = [
            ApiResponse(description = "employee not found", responseCode = "404"),
            ApiResponse(description = "employee deleted", responseCode = "204"),
            ApiResponse(description = "only admin can delete an employee", responseCode = "403"),
        ]
    )
    @DeleteMapping("/{employeeId}")
    fun deleteEmployee(@PathVariable employeeId: UUID): ResponseEntity<Any> {
        employeeService.delete(employeeId)
        return ResponseEntity.noContent().build()
    }

}