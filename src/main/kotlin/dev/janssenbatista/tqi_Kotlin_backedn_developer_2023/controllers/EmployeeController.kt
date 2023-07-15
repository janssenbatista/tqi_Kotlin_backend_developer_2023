package dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.controllers

import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.dtos.UserRequestDto
import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.dtos.UserResponseDto
import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.services.EmployeeService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/employees")
class EmployeeController(private val employeeService: EmployeeService) {

    @PostMapping
    fun createEmployee(@RequestBody @Valid userRequestDto: UserRequestDto): ResponseEntity<UserResponseDto> {
        val createdEmployee = employeeService.save(userRequestDto)
        return ResponseEntity.status(HttpStatus.CREATED).body(UserResponseDto.fromEntity(createdEmployee))
    }

    @GetMapping("/{employeeId}/profile")
    fun getProfile(@PathVariable employeeId: UUID): ResponseEntity<UserResponseDto> {
        val foundEmployee = employeeService.findById(employeeId)
        return ResponseEntity.ok(UserResponseDto.fromEntity(foundEmployee))
    }

    @PutMapping("/{employeeId}")
    fun updateEmployee(@PathVariable employeeId: UUID,
                       @RequestBody @Valid userRequestDto: UserRequestDto): ResponseEntity<UserResponseDto> {
        val updatedEmployee = employeeService.update(employeeId, userRequestDto)
        return ResponseEntity.ok(UserResponseDto.fromEntity(updatedEmployee))
    }

    @DeleteMapping("/{employeeId}")
    fun deleteEmployee(@PathVariable employeeId: UUID): ResponseEntity<Any> {
        employeeService.delete(employeeId)
        return ResponseEntity.noContent().build()
    }

}