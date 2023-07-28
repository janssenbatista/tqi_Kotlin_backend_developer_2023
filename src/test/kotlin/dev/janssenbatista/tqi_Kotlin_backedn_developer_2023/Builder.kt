package dev.janssenbatista.tqi_Kotlin_backedn_developer_2023

import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.dtos.*
import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.enums.PaymentMethod
import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.enums.Role
import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.models.*
import java.math.BigDecimal
import java.time.ZonedDateTime
import java.util.*

fun buildUser(
    id: UUID = UUID.randomUUID(),
    name: String = "user name",
    email: String = "user@email.com",
    password: String = "encryptedHashPassword",
    roleId: Int,
    isEnabled: Boolean = true,
    createdAt: ZonedDateTime = ZonedDateTime.now(),
    updatedAt: ZonedDateTime = ZonedDateTime.now()
): User = User(
    id = id,
    name = name,
    email = email,
    password = password,
    roleId = roleId,
    isEnabled = isEnabled,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun buildUserRequestDto(
    name: String = "user name",
    email: String = "user@email.com",
    password: String = "11111111"
): UserRequestDto {
    return UserRequestDto(
        name = name,
        email = email,
        password = password
    )
}

fun buildProductRequestDto(): ProductRequestDto {
    return ProductRequestDto(
        name = "Product Name",
        measurementUnit = "UND",
        unitPrice = BigDecimal.valueOf(1.0),
        quantityInStock = 1,
        categoryId = 1
    )
}


fun buildProduct(): Product {
    return Product(id = UUID.randomUUID(), name = "Product Name",
        measurementUnit = "UND",
        unitPrice = BigDecimal.valueOf(1.0),
        quantityInStock = 1,
        category = Category(id = 1, name = "Category Name"),
        createdAt = ZonedDateTime.now(),
        updatedAt = ZonedDateTime.now()
    )
}

fun buildShoppingCart(customer: User = buildUser(roleId = Role.CUSTOMER.value)): ShoppingCart {
    return ShoppingCart(id = UUID.randomUUID(), customer = customer)
}

fun buildShoppingCartResponseDto(shoppingCart: ShoppingCart): ShoppingCartResponseDto {
    return ShoppingCartResponseDto(shoppingCartId = shoppingCart.id!!,
        customerId = shoppingCart.customer.id!!)
}

fun buildItem(shoppingCart: ShoppingCart = buildShoppingCart()): Item {
    return Item(
        id = UUID.randomUUID(),
        shoppingCart = shoppingCart,
        product = buildProduct(),
        quantity = 1
    )
}

fun buildCheckoutResponseDto(paymentMethod: PaymentMethod): CheckoutResponseDto {
    return CheckoutResponseDto(
        shoppingCartId = UUID.randomUUID(),
        customerName = "customer name",
        totalValue = BigDecimal.ONE,
        paymentMethod = paymentMethod,
        paidAt = ZonedDateTime.now()
    )
}