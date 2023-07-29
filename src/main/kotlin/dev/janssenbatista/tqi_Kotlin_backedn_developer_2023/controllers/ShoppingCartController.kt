package dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.controllers

import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.dtos.*
import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.services.ShoppingCartService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import jakarta.validation.constraints.Min
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/shopping-carts")
@Tag(name = "Shopping Cart")
@SecurityRequirement(name = "bearerAuth")
class ShoppingCartController(private val shoppingCartService: ShoppingCartService) {

    @Operation(
        summary = "create a shopping cart",
        responses = [
            ApiResponse(description = "customer not found", responseCode = "404"),
            ApiResponse(description = "shopping cart created", responseCode = "201")
        ]
    )
    @PostMapping
    fun create(@RequestBody @Valid dto: ShoppingCartDto): ResponseEntity<ShoppingCartResponseDto> {
        val createdShoppingCart = shoppingCartService.create(dto)
        return ResponseEntity.status(HttpStatus.CREATED).body(createdShoppingCart)
    }

    @Operation(
        summary = "get shopping cart by id",
        responses = [
            ApiResponse(description = "shopping cart not found", responseCode = "404"),
            ApiResponse(description = "shopping cart info", responseCode = "200"),
            ApiResponse(description = "only shopping cart owner can access this info", responseCode = "403")
        ]
    )
    @GetMapping("/{id}")
    fun getShoppingCartById(@PathVariable id: UUID): ResponseEntity<ShoppingCartResponseDto> {
        val foundShoppingCart = shoppingCartService.findById(id)
        return ResponseEntity.ok(foundShoppingCart)
    }

    @Operation(
        summary = "shopping cart checkout",
        responses = [
            ApiResponse(description = "shopping cart not found", responseCode = "404"),
            ApiResponse(description = "only shopping cart owner can access this info", responseCode = "403"),
            ApiResponse(description = "shopping cart has no items", responseCode = "400"),
            ApiResponse(description = "shopping cart checked out", responseCode = "200")
        ]
    )
    @PutMapping("/{id}/checkout")
    fun checkoutShoppingCart(@PathVariable id: UUID, @RequestBody dto: CheckoutRequestDto): CheckoutResponseDto {
        return shoppingCartService.checkout(id, dto.paymentMethod)
    }

    @Operation(
        summary = "delete shopping cart by id",
        responses = [
            ApiResponse(description = "shopping cart not found", responseCode = "404"),
            ApiResponse(description = "only shopping cart owner can access this info", responseCode = "403"),
            ApiResponse(description = "shopping cart contains items and cannot be deleted / " +
                "cannot delete a paid shopping cart", responseCode = "400"),
            ApiResponse(description = "shopping cart deleted", responseCode = "204")
        ]
    )
    @DeleteMapping("/{id}")
    fun deleteShoppingCart(@PathVariable id: UUID): ResponseEntity<Any> {
        shoppingCartService.delete(id)
        return ResponseEntity.noContent().build()
    }

    @Operation(
        summary = "add an item to a shopping cart",
        responses = [
            ApiResponse(description = "shopping cart not found / product not found", responseCode = "404"),
            ApiResponse(description = "only shopping cart owner can add an item", responseCode = "403"),
            ApiResponse(description = "cannot add an item to a paid shopping cart / " +
                "product has not sufficient quantity in stock", responseCode = "400"),
            ApiResponse(description = "product already exists in shopping cart", responseCode = "409")
        ]
    )
    @PostMapping("/{id}/items")
    fun addItem(@PathVariable id: UUID, @RequestBody @Valid dto: ItemRequestDto): ResponseEntity<ItemResponseDto> {
        val addedItem = shoppingCartService.addItem(id, dto)
        return ResponseEntity.status(HttpStatus.CREATED).body(addedItem)
    }

    @Operation(
        summary = "get all item of a shopping cart",
        responses = [
            ApiResponse(description = "shopping cart not found", responseCode = "404"),
            ApiResponse(description = "only shopping cart owner can access this information", responseCode = "403"),
            ApiResponse(description = "shopping cart items", responseCode = "200")
        ]
    )
    @GetMapping("/{id}/items")
    fun getAllItems(@PathVariable id: UUID): ResponseEntity<List<ItemResponseDto>> {
        return ResponseEntity.ok(shoppingCartService.getAllItems(id))
    }

    @Operation(
        summary = "delete an item of a shopping cart",
        responses = [
            ApiResponse(description = "shopping cart not found / item not found", responseCode = "404"),
            ApiResponse(description = "only shopping cart owner can delete an item", responseCode = "403"),
            ApiResponse(description = "cannot delete an item of a paid shopping cart", responseCode = "400"),
            ApiResponse(description = "item deleted", responseCode = "204")
        ]
    )
    @DeleteMapping("/{id}/items/{itemId}")
    fun deleteItem(@PathVariable id: UUID, @PathVariable itemId: UUID): ResponseEntity<Any> {
        shoppingCartService.deleteItem(id, itemId)
        return ResponseEntity.noContent().build()
    }

    @Operation(
        summary = "update quantity of an item",
        responses = [
            ApiResponse(description = "shopping cart not found / item not found", responseCode = "404"),
            ApiResponse(description = "only shopping cart owner can update item quantity", responseCode = "403"),
            ApiResponse(description = "the product has not sufficient quantity in stock", responseCode = "400"),
            ApiResponse(description = "item quantity updated", responseCode = "200")
        ]
    )
    @PatchMapping("/{id}/items/{itemId}")
    fun updateItemQuantity(@PathVariable id: UUID,
                           @PathVariable itemId: UUID,
                           @RequestBody @Valid
                           @Min(value = 1, message = "quantity cannot be less than 1") quantity: Int): ResponseEntity<ItemResponseDto> {
        val updatedItem = shoppingCartService.updateItemQuantity(id, itemId, quantity)
        return ResponseEntity.ok(updatedItem)
    }


}