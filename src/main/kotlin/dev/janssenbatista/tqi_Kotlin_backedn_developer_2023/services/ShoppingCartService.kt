package dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.services

import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.dtos.*
import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.enums.PaymentMethod
import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.enums.ShoppingCartStatus
import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.exceptions.*
import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.models.Item
import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.models.ShoppingCart
import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.repositories.ItemRepository
import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.repositories.ProductRepository
import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.repositories.ShoppingCartRepository
import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.repositories.UserRepository
import jakarta.transaction.Transactional
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import java.time.ZonedDateTime
import java.util.*

@Service
class ShoppingCartService(private val userRepository: UserRepository,
                          private val shoppingCartRepository: ShoppingCartRepository,
                          private val productRepository: ProductRepository,
                          private val itemRepository: ItemRepository) {
    fun create(dto: ShoppingCartDto): ShoppingCartResponseDto {
        val customer = userRepository.findById(UUID.fromString(dto.customerId)).orElseThrow {
            CustomerNotFoundException("customer not found")
        }
        val shoppingCart = ShoppingCart(
            customer = customer
        )
        val createdShoppingCart = shoppingCartRepository.save(shoppingCart)
        return ShoppingCartResponseDto(shoppingCartId = createdShoppingCart.id!!,
            customerId = createdShoppingCart.customer.id!!)
    }

    fun findById(shoppingCartId: UUID): ShoppingCartResponseDto {
        val shoppingCart = shoppingCartRepository.findById(shoppingCartId).orElseThrow {
            ShoppingCartNotFoundException("shopping cart not found")
        }
        verifyAccessPermission(shoppingCart.customer.id!!)
        return ShoppingCartResponseDto(shoppingCartId = shoppingCart.id!!,
            customerId = shoppingCart.customer.id)
    }

    fun checkout(shoppingCartId: UUID, paymentMethod: PaymentMethod): CheckoutResponseDto {
        val shoppingCart = shoppingCartRepository.findById(shoppingCartId).orElseThrow {
            ShoppingCartNotFoundException("shopping cart not found")
        }
        verifyAccessPermission(shoppingCart.customer.id!!)
        val itemsList = itemRepository.findAllByShoppingCartId(shoppingCartId)
        if (itemsList.isEmpty()) {
            throw BadRequestException("shopping cart has no items")
        }
        val totalValue = shoppingCartRepository.getTotal(shoppingCartId)
        shoppingCart.apply {
            this.paymentMethod = paymentMethod
            status = ShoppingCartStatus.PAID
            updatedAt = ZonedDateTime.now()
        }
        return CheckoutResponseDto(
            shoppingCartId = shoppingCart.id!!,
            customerName = shoppingCart.customer.name,
            totalValue = totalValue,
            paymentMethod = shoppingCart.paymentMethod!!,
            paidAt = ZonedDateTime.now()
        )
    }

    fun delete(shoppingCartId: UUID) {
        val shoppingCart = shoppingCartRepository.findById(shoppingCartId).orElseThrow {
            ShoppingCartNotFoundException("shopping cart not found")
        }
        verifyAccessPermission(shoppingCart.customer.id!!)
        val itemsList = itemRepository.findAllByShoppingCartId(shoppingCartId)
        if (itemsList.isNotEmpty()) {
            throw BadRequestException("shopping cart contains items and cannot be deleted")
        }
        if (shoppingCart.status == ShoppingCartStatus.PAID) {
            throw BadRequestException("you cannot delete a paid cart")
        }
        shoppingCartRepository.delete(shoppingCart)
    }

    @Transactional
    fun addItem(shoppingCartId: UUID, dto: ItemRequestDto): ItemResponseDto {
        val shoppingCart = shoppingCartRepository.findById(shoppingCartId).orElseThrow {
            ShoppingCartNotFoundException("shopping cart not found")
        }
        verifyAccessPermission(shoppingCart.customer.id!!)
        verifyIfShoppingCartIsPaid(shoppingCart, message = "you cannot add item in a paid shopping cart")
        val product = productRepository.findById(UUID.fromString(dto.productId)).orElseThrow {
            ProductNotFoundException("product not found")
        }
        val itemAlreadyExists = itemRepository.findByShoppingCartIdAndProductId(shoppingCartId, UUID.fromString(dto.productId))
        if (itemAlreadyExists.isPresent) {
            throw ItemAlreadyExistsException("this product already exists in this shopping cart")
        }
        if (dto.quantity > product.quantityInStock) {
            throw InsufficientStockException("the product has not sufficient quantity in stock")
        }
        product.apply {
            quantityInStock -= dto.quantity
            updatedAt = ZonedDateTime.now()
        }
        productRepository.save(product)
        val item = Item(shoppingCart = shoppingCart, product = product, quantity = dto.quantity)
        shoppingCart.updatedAt = ZonedDateTime.now()
        val savedItem = itemRepository.save(item)
        return ItemResponseDto(savedItem.id!!, savedItem.shoppingCart.id!!, savedItem.product.name, savedItem.quantity)
    }

    fun getAllItems(shoppingCartId: UUID): List<ItemResponseDto> {
        val shoppingCart = shoppingCartRepository.findById(shoppingCartId).orElseThrow {
            throw ShoppingCartNotFoundException("shopping cart not found")
        }
        verifyAccessPermission(shoppingCart.customer.id!!)
        val items = itemRepository.findAllByShoppingCartId(shoppingCart.id!!)
        return items.map {
            ItemResponseDto(it.id!!, it.shoppingCart.id!!, it.product.name, it.quantity)
        }
    }

    @Transactional
    fun deleteItem(shoppingCartId: UUID, itemId: UUID) {
        val shoppingCart = shoppingCartRepository.findById(shoppingCartId).orElseThrow {
            ShoppingCartNotFoundException("shopping cart not found")
        }
        verifyAccessPermission(shoppingCart.customer.id!!)
        verifyIfShoppingCartIsPaid(shoppingCart, message = "you cannot delete an item of a paid shopping cart")
        val item = itemRepository.findById(itemId).orElseThrow {
            ItemNotFoundException("item not found")
        }
        if (shoppingCart.id!! != item.shoppingCart.id!!) {
            throw ItemNotFoundException("item not found")
        }
        val product = productRepository.findById(item.product.id!!)
        product.get().apply {
            quantityInStock += item.quantity
            updatedAt = ZonedDateTime.now()
        }
        productRepository.save(product.get())
        shoppingCart.updatedAt = ZonedDateTime.now()
        itemRepository.delete(item)
    }

    @Transactional
    fun updateItemQuantity(shoppingCartId: UUID, itemId: UUID, quantity: Int): ItemResponseDto {
        val shoppingCart = shoppingCartRepository.findById(shoppingCartId).orElseThrow {
            ShoppingCartNotFoundException("shopping cart not found")
        }
        verifyAccessPermission(shoppingCart.customer.id!!)
        verifyIfShoppingCartIsPaid(shoppingCart, message = "you cannot update item quantity of a paid shopping cart")
        val item = itemRepository.findById(itemId).orElseThrow {
            ItemNotFoundException("item not found")
        }
        if (shoppingCart.id!! != item.shoppingCart.id!!) {
            throw ItemNotFoundException("item not found")
        }
        val product = productRepository.findById(item.product.id!!).orElseThrow { BadRequestException(null) }
        product.quantityInStock += item.quantity
        if (quantity > product.quantityInStock) {
            throw InsufficientStockException("the product has not sufficient quantity in stock")
        }
        product.quantityInStock -= quantity
        productRepository.save(product)
        item.apply {
            this.quantity = quantity
            updatedAt = ZonedDateTime.now()
        }
        shoppingCart.updatedAt = ZonedDateTime.now()
        val updatedItem = itemRepository.save(item)
        return ItemResponseDto(updatedItem.id!!,
            updatedItem.shoppingCart.id!!,
            updatedItem.product.name,
            updatedItem.quantity)
    }


    private fun verifyAccessPermission(customerId: UUID) {
        if (SecurityContextHolder.getContext().authentication != null) {
            val customer = userRepository.findById(customerId)
            val currentUser = SecurityContextHolder.getContext().authentication.name
            if (customer.get().email != currentUser) {
                throw ForbiddenException("you cannot access this data")
            }
        }
    }

    private fun verifyIfShoppingCartIsPaid(shoppingCart: ShoppingCart, message: String) {
        if (shoppingCart.status.name == "PAID") {
            throw BadRequestException(message)
        }
    }

}