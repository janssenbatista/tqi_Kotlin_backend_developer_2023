package dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.controllers

import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.dtos.*
import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.models.Item
import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.models.ShoppingCart
import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.services.ShoppingCartService
import jakarta.validation.Valid
import jakarta.validation.constraints.Min
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/shopping-carts")
class ShoppingCartController(private val shoppingCartService: ShoppingCartService) {

    @PostMapping
    fun create(@RequestBody @Valid dto: ShoppingCartDto): ResponseEntity<ShoppingCartResponseDto> {
        val createdShoppingCart = shoppingCartService.create(dto)
        return ResponseEntity.status(HttpStatus.CREATED).body(createdShoppingCart)
    }

    @GetMapping("/{id}")
    fun getShoppingCartById(@PathVariable id: UUID): ResponseEntity<ShoppingCartResponseDto> {
        val foundShoppingCart = shoppingCartService.findById(id)
        return ResponseEntity.ok(foundShoppingCart)
    }

    @PutMapping("/{id}/checkout")
    fun checkoutShoppingCart(@PathVariable id: UUID, @RequestBody dto: CheckoutRequestDto): CheckoutResponseDto {
        return shoppingCartService.checkout(id, dto.paymentMethod)
    }

    @DeleteMapping("/{id}")
    fun deleteShoppingCart(@PathVariable id: UUID): ResponseEntity<Any> {
        shoppingCartService.delete(id)
        return ResponseEntity.noContent().build()
    }

    @PostMapping("/{id}/items")
    fun addItem(@PathVariable id: UUID, @RequestBody @Valid dto: ItemRequestDto): ResponseEntity<ItemResponseDto> {
        val addedItem = shoppingCartService.addItem(id, dto)
        return ResponseEntity.status(HttpStatus.CREATED).body(addedItem)
    }

    @GetMapping("/{id}/items")
    fun getAllItems(@PathVariable id: UUID): ResponseEntity<List<ItemResponseDto>> {
        return ResponseEntity.ok(shoppingCartService.getAllItems(id))
    }

    @DeleteMapping("/{id}/items/{itemId}")
    fun deleteItem(@PathVariable id: UUID, @PathVariable itemId: UUID): ResponseEntity<Any> {
        shoppingCartService.deleteItem(id, itemId)
        return ResponseEntity.noContent().build()
    }

    @PatchMapping("/{id}/items/{itemId}")
    fun updateItemQuantity(@PathVariable id: UUID,
                           @PathVariable itemId: UUID,
                           @RequestBody @Valid
                           @Min(value = 1, message = "quantity cannot be less than 1") quantity: Int): ResponseEntity<ItemResponseDto> {
        val updatedItem = shoppingCartService.updateItemQuantity(id, itemId, quantity)
        return ResponseEntity.ok(updatedItem)
    }


}