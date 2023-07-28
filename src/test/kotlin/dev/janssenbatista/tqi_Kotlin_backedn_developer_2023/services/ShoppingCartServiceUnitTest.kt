package dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.services

import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.buildItem
import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.buildProduct
import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.buildShoppingCart
import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.buildUser
import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.dtos.ItemRequestDto
import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.dtos.ShoppingCartDto
import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.enums.PaymentMethod
import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.enums.Role
import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.enums.ShoppingCartStatus
import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.exceptions.*
import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.models.Item
import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.models.Product
import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.models.ShoppingCart
import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.models.User
import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.repositories.ItemRepository
import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.repositories.ProductRepository
import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.repositories.ShoppingCartRepository
import dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.repositories.UserRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

class ShoppingCartServiceUnitTest {

    private val userRepository: UserRepository = mockk()
    private val shoppingCartRepository: ShoppingCartRepository = mockk()
    private val productRepository: ProductRepository = mockk()
    private val itemRepository: ItemRepository = mockk()
    private val shoppingCartService = ShoppingCartService(userRepository,
        shoppingCartRepository,
        productRepository,
        itemRepository)
    private lateinit var shoppingCart: ShoppingCart
    private lateinit var customer: User
    private lateinit var product: Product
    private lateinit var item: Item
    private lateinit var dto: ShoppingCartDto
    private lateinit var itemDto: ItemRequestDto

    @BeforeEach
    fun setup() {
        product = buildProduct()
        shoppingCart = buildShoppingCart()
        item = buildItem(shoppingCart)
        customer = buildUser(roleId = Role.CUSTOMER.value)
        dto = ShoppingCartDto(customerId = customer.id!!.toString())
        itemDto = ItemRequestDto(productId = product.id!!.toString(), quantity = 1)
    }

    // create
    @Test
    fun `should be able to create a shopping cart`() {
        every { userRepository.findById(any()) } returns Optional.of(customer)
        every { shoppingCartRepository.save(any()) } returns shoppingCart
        shoppingCartService.create(dto)
        verify(exactly = 1) { userRepository.findById(any()) }
        verify(exactly = 1) { shoppingCartRepository.save(any()) }
    }

    // findById
    @Test
    fun `should be able to find a shopping cart by id`() {
        every { shoppingCartRepository.findById(any()) } returns Optional.of(shoppingCart)
        val foundShoppingCart = shoppingCartService.findById(shoppingCart.id!!)
        assertThat(foundShoppingCart).isNotNull
        verify(exactly = 1) { shoppingCartRepository.findById(any()) }
    }

    @Test
    fun `should not be able to find a shopping cart by id`() {
        val message = "shopping cart not found"
        every { shoppingCartRepository.findById(any()) } throws ShoppingCartNotFoundException(message)
        assertThatExceptionOfType(ShoppingCartNotFoundException::class.java).isThrownBy {
            shoppingCartService.findById(shoppingCart.id!!)
        }.withMessage(message)
        verify(exactly = 1) { shoppingCartRepository.findById(any()) }
    }

    // checkout
    @Test
    fun `should be able to checkout a shopping cart`() {
        val itemsList = listOf(buildItem())
        every { shoppingCartRepository.findById(any()) } returns Optional.of(shoppingCart)
        every { itemRepository.findAllByShoppingCartId(any()) } returns itemsList
        every { shoppingCartRepository.getTotal(any()) } returns itemsList[0].product.unitPrice
        val responseDto = shoppingCartService.checkout(shoppingCart.id!!, paymentMethod = PaymentMethod.DEBIT_CARD)
        assertThat(shoppingCart.paymentMethod).isEqualTo(PaymentMethod.DEBIT_CARD)
        assertThat(shoppingCart.status).isEqualTo(ShoppingCartStatus.PAID)
        assertThat(responseDto).isNotNull
        verify(exactly = 1) { shoppingCartRepository.findById(any()) }
        verify(exactly = 1) { itemRepository.findAllByShoppingCartId(any()) }
        verify(exactly = 1) { shoppingCartRepository.getTotal(any()) }
    }

    @Test
    fun `should not be able to checkout a shopping cart when item list is empty`() {
        every { shoppingCartRepository.findById(any()) } returns Optional.of(shoppingCart)
        every { itemRepository.findAllByShoppingCartId(any()) } returns emptyList()
        assertThatExceptionOfType(BadRequestException::class.java).isThrownBy {
            shoppingCartService.checkout(shoppingCart.id!!, paymentMethod = PaymentMethod.DEBIT_CARD)
        }.withMessage("shopping cart has no items")
        verify(exactly = 1) { shoppingCartRepository.findById(any()) }
        verify(exactly = 1) { itemRepository.findAllByShoppingCartId(any()) }
        verify(exactly = 0) { shoppingCartRepository.getTotal(any()) }
    }

    // delete
    @Test
    fun `should be able do delete a shopping cart when list of items is empty`() {
        every { shoppingCartRepository.findById(any()) } returns Optional.of(shoppingCart)
        every { itemRepository.findAllByShoppingCartId(any()) } returns emptyList()
        every { shoppingCartRepository.delete(any()) } returns Unit
        shoppingCartService.delete(shoppingCart.id!!)
        verify(exactly = 1) { shoppingCartRepository.findById(any()) }
        verify(exactly = 1) { itemRepository.findAllByShoppingCartId(any()) }
        verify(exactly = 1) { shoppingCartRepository.delete(any()) }
    }

    @Test
    fun `should not be able do delete a shopping cart when it has items`() {
        shoppingCart.status = ShoppingCartStatus.PAID
        every { shoppingCartRepository.findById(any()) } returns Optional.of(shoppingCart)
        every { itemRepository.findAllByShoppingCartId(any()) } returns emptyList()
        assertThatExceptionOfType(BadRequestException::class.java).isThrownBy {
            shoppingCartService.delete(shoppingCart.id!!)
        }.withMessage("you cannot delete a paid cart")
        verify(exactly = 1) { shoppingCartRepository.findById(any()) }
        verify(exactly = 1) { itemRepository.findAllByShoppingCartId(any()) }
        verify(exactly = 0) { shoppingCartRepository.delete(any()) }
    }

    @Test
    fun `should not be able do delete a shopping cart when status is PAID`() {
        every { shoppingCartRepository.findById(any()) } returns Optional.of(shoppingCart)
        every { itemRepository.findAllByShoppingCartId(any()) } returns listOf(buildItem())
        assertThatExceptionOfType(BadRequestException::class.java).isThrownBy {
            shoppingCartService.delete(shoppingCart.id!!)
        }.withMessage("shopping cart contains items and cannot be deleted")
        verify(exactly = 1) { shoppingCartRepository.findById(any()) }
        verify(exactly = 1) { itemRepository.findAllByShoppingCartId(any()) }
        verify(exactly = 0) { shoppingCartRepository.delete(any()) }
    }

    // getAllItems
    @Test
    fun `should be able to get all items of a shopping cart`() {
        shoppingCart.items.add(buildItem())
        every { shoppingCartRepository.findById(any()) } returns Optional.of(shoppingCart)
        every { itemRepository.findAllByShoppingCartId(any()) } returns shoppingCart.items
        val items = shoppingCartService.getAllItems(shoppingCart.id!!)
        assertThat(items.size).isEqualTo(1)
        verify(exactly = 1) { shoppingCartRepository.findById(any()) }
        verify(exactly = 1) { itemRepository.findAllByShoppingCartId(any()) }
    }

    // addItem
    @Test
    fun `should be able do add an item`() {
        val quantityInStock = product.quantityInStock
        val lastUpdate = product.updatedAt
        every { shoppingCartRepository.findById(any()) } returns Optional.of(shoppingCart)
        every { productRepository.findById(any()) } returns Optional.of(product)
        every { itemRepository.findByShoppingCartIdAndProductId(any(), any()) } returns Optional.empty()
        every { itemRepository.save(any()) } returns item
        every { productRepository.save(any()) } returns product
        shoppingCartService.addItem(shoppingCartId = shoppingCart.id!!, dto = itemDto)
        assertThat(product.quantityInStock).isNotEqualTo(quantityInStock)
        assertThat(product.updatedAt).isNotEqualTo(lastUpdate)
        verify(exactly = 1) { shoppingCartRepository.findById(any()) }
        verify(exactly = 1) { productRepository.findById(any()) }
        verify(exactly = 1) { itemRepository.findByShoppingCartIdAndProductId(any(), any()) }
        verify(exactly = 1) { itemRepository.save(any()) }
        verify(exactly = 1) { productRepository.save(any()) }
    }

    @Test
    fun `should not be able do add an item when shopping cart does not exists`() {
        val message = "shopping cart not found"
        every { shoppingCartRepository.findById(any()) } throws ShoppingCartNotFoundException(message)
        assertThatExceptionOfType(ShoppingCartNotFoundException::class.java).isThrownBy {
            shoppingCartService.addItem(shoppingCartId = shoppingCart.id!!, dto = itemDto)
        }.withMessage(message)
        verify(exactly = 1) { shoppingCartRepository.findById(any()) }
        verify(exactly = 0) { productRepository.findById(any()) }
        verify(exactly = 0) { itemRepository.findByShoppingCartIdAndProductId(any(), any()) }
        verify(exactly = 0) { itemRepository.save(any()) }
        verify(exactly = 0) { productRepository.save(any()) }
    }

    @Test
    fun `should not be able do add an item when shopping cart status is PAID`() {
        val message = "you cannot add item in a paid shopping cart"
        shoppingCart.status = ShoppingCartStatus.PAID
        every { shoppingCartRepository.findById(any()) } returns Optional.of(shoppingCart)
        assertThatExceptionOfType(BadRequestException::class.java).isThrownBy {
            shoppingCartService.addItem(shoppingCart.id!!, itemDto)
        }.withMessage(message)
        verify(exactly = 1) { shoppingCartRepository.findById(any()) }
        verify(exactly = 0) { productRepository.findById(any()) }
        verify(exactly = 0) { itemRepository.findByShoppingCartIdAndProductId(any(), any()) }
        verify(exactly = 0) { itemRepository.save(any()) }
        verify(exactly = 0) { productRepository.save(any()) }
    }

    @Test
    fun `should not be able do add an item when product does not exists`() {
        val message = "product not found"
        every { shoppingCartRepository.findById(any()) } returns Optional.of(shoppingCart)
        every { productRepository.findById(any()) } throws ProductNotFoundException(message)
        assertThatExceptionOfType(ProductNotFoundException::class.java).isThrownBy {
            shoppingCartService.addItem(shoppingCartId = shoppingCart.id!!, dto = itemDto)
        }.withMessage(message)
        verify(exactly = 1) { shoppingCartRepository.findById(any()) }
        verify(exactly = 1) { productRepository.findById(any()) }
        verify(exactly = 0) { itemRepository.findByShoppingCartIdAndProductId(any(), any()) }
        verify(exactly = 0) { itemRepository.save(any()) }
        verify(exactly = 0) { productRepository.save(any()) }
    }

    @Test
    fun `should not be able do add an item when it already exists`() {
        val message = "this product already exists in this shopping cart"
        every { shoppingCartRepository.findById(any()) } returns Optional.of(shoppingCart)
        every { productRepository.findById(any()) } returns Optional.of(product)
        every { itemRepository.findByShoppingCartIdAndProductId(any(), any()) } throws ItemAlreadyExistsException(message)
        assertThatExceptionOfType(ItemAlreadyExistsException::class.java).isThrownBy {
            shoppingCartService.addItem(shoppingCartId = shoppingCart.id!!, dto = itemDto)
        }.withMessage(message)
        verify(exactly = 1) { shoppingCartRepository.findById(any()) }
        verify(exactly = 1) { productRepository.findById(any()) }
        verify(exactly = 1) { itemRepository.findByShoppingCartIdAndProductId(any(), any()) }
        verify(exactly = 0) { itemRepository.save(any()) }
        verify(exactly = 0) { productRepository.save(any()) }
    }

    @Test
    fun `should be able do add an item when product has not sufficient stock`() {
        val message = "the product has not sufficient quantity in stock"
        product.quantityInStock = 0
        every { shoppingCartRepository.findById(any()) } returns Optional.of(shoppingCart)
        every { productRepository.findById(any()) } returns Optional.of(product)
        every { itemRepository.findByShoppingCartIdAndProductId(any(), any()) } returns Optional.empty()
        assertThatExceptionOfType(InsufficientStockException::class.java).isThrownBy {
            shoppingCartService.addItem(shoppingCartId = shoppingCart.id!!, dto = itemDto)
        }.withMessage(message)
        verify(exactly = 1) { shoppingCartRepository.findById(any()) }
        verify(exactly = 1) { productRepository.findById(any()) }
        verify(exactly = 1) { itemRepository.findByShoppingCartIdAndProductId(any(), any()) }
        verify(exactly = 0) { itemRepository.save(any()) }
        verify(exactly = 0) { productRepository.save(any()) }
    }

    // deleteItem
    @Test
    fun `should be able to delete an item`() {
        val quantityInStock = product.quantityInStock
        val lastUpdate = product.updatedAt
        every { shoppingCartRepository.findById(any()) } returns Optional.of(shoppingCart)
        every { itemRepository.findById(any()) } returns Optional.of(item)
        every { productRepository.findById(any()) } returns Optional.of(product)
        every { productRepository.save(any()) } returns product
        every { itemRepository.delete(any()) } returns Unit
        shoppingCartService.deleteItem(shoppingCart.id!!, item.id!!)
        assertThat(product.quantityInStock).isGreaterThan(quantityInStock)
        assertThat(product.updatedAt).isNotEqualTo(lastUpdate)
        verify(exactly = 1) { shoppingCartRepository.findById(any()) }
        verify(exactly = 1) { itemRepository.findById(any()) }
        verify(exactly = 1) { productRepository.findById(any()) }
        verify(exactly = 1) { productRepository.save(any()) }
        verify(exactly = 1) { itemRepository.delete(any()) }
    }

    @Test
    fun `should not be able to delete an item when shopping cart does not exists`() {
        val message = "shopping cart not found"
        every { shoppingCartRepository.findById(any()) } throws ShoppingCartNotFoundException(message)
        assertThatExceptionOfType(ShoppingCartNotFoundException::class.java).isThrownBy {
            shoppingCartService.deleteItem(shoppingCartId = shoppingCart.id!!, itemId = item.id!!)
        }.withMessage(message)
        verify(exactly = 1) { shoppingCartRepository.findById(any()) }
        verify(exactly = 0) { itemRepository.findById(any()) }
        verify(exactly = 0) { productRepository.findById(any()) }
        verify(exactly = 0) { productRepository.save(any()) }
        verify(exactly = 0) { itemRepository.delete(any()) }
    }

    @Test
    fun `should not be able do delete an item when shopping cart status is PAID`() {
        val message = "you cannot delete an item of a paid shopping cart"
        shoppingCart.status = ShoppingCartStatus.PAID
        every { shoppingCartRepository.findById(any()) } returns Optional.of(shoppingCart)
        assertThatExceptionOfType(BadRequestException::class.java).isThrownBy {
            shoppingCartService.deleteItem(shoppingCart.id!!, item.id!!)
        }.withMessage(message)
        verify(exactly = 1) { shoppingCartRepository.findById(any()) }
        verify(exactly = 0) { productRepository.findById(any()) }
        verify(exactly = 0) { itemRepository.findByShoppingCartIdAndProductId(any(), any()) }
        verify(exactly = 0) { itemRepository.save(any()) }
        verify(exactly = 0) { productRepository.save(any()) }
    }

    @Test
    fun `should not be able to delete a non-existing item`() {
        val message = "item not found"
        every { shoppingCartRepository.findById(any()) } returns Optional.of(shoppingCart)
        every { itemRepository.findById(any()) } throws ItemNotFoundException(message)
        assertThatExceptionOfType(ItemNotFoundException::class.java).isThrownBy {
            shoppingCartService.deleteItem(shoppingCartId = shoppingCart.id!!, itemId = item.id!!)
        }.withMessage(message)
        verify(exactly = 1) { shoppingCartRepository.findById(any()) }
        verify(exactly = 1) { itemRepository.findById(any()) }
        verify(exactly = 0) { productRepository.findById(any()) }
        verify(exactly = 0) { productRepository.save(any()) }
        verify(exactly = 0) { itemRepository.delete(any()) }
    }

    @Test
    fun `should not be able to delete an item when the item does not belong to the shopping cart`() {
        val message = "item not found"
        item.shoppingCart = buildShoppingCart().apply { id = UUID.randomUUID() }
        every { shoppingCartRepository.findById(any()) } returns Optional.of(shoppingCart)
        every { itemRepository.findById(any()) } returns Optional.of(item)
        assertThatExceptionOfType(BadRequestException::class.java).isThrownBy {
            shoppingCartService.deleteItem(shoppingCartId = shoppingCart.id!!, itemId = item.id!!)
        }.withMessage(message)
        verify(exactly = 1) { shoppingCartRepository.findById(any()) }
        verify(exactly = 1) { itemRepository.findById(any()) }
        verify(exactly = 0) { productRepository.findById(any()) }
        verify(exactly = 0) { productRepository.save(any()) }
        verify(exactly = 0) { itemRepository.delete(any()) }
    }

    // updateItemQuantity
    @Test
    fun `should be able to update item quantity`() {
        val quantityToUpdate = 2
        every { shoppingCartRepository.findById(any()) } returns Optional.of(shoppingCart)
        every { itemRepository.findById(any()) } returns Optional.of(item)
        every { productRepository.findById(any()) } returns Optional.of(product)
        every { productRepository.save(any()) } returns product
        every { itemRepository.save(any()) } returns item
        shoppingCartService.updateItemQuantity(shoppingCart.id!!, item.id!!, quantityToUpdate)
        verify(exactly = 1) { shoppingCartRepository.findById(any()) }
        verify(exactly = 1) { itemRepository.findById(any()) }
        verify(exactly = 1) { productRepository.findById(any()) }
        verify(exactly = 1) { productRepository.save(any()) }
        verify(exactly = 1) { itemRepository.save(any()) }
    }

    @Test
    fun `should not be able to update item quantity when shopping cart does not exists`() {
        val message = "shopping cart not found"
        every { shoppingCartRepository.findById(any()) } throws ShoppingCartNotFoundException(message)
        assertThatExceptionOfType(ShoppingCartNotFoundException::class.java).isThrownBy {
            shoppingCartService.updateItemQuantity(shoppingCartId = shoppingCart.id!!, itemId = item.id!!, 1)
        }.withMessage(message)
        verify(exactly = 1) { shoppingCartRepository.findById(any()) }
        verify(exactly = 0) { itemRepository.findById(any()) }
        verify(exactly = 0) { productRepository.findById(any()) }
        verify(exactly = 0) { productRepository.save(any()) }
        verify(exactly = 0) { itemRepository.save(any()) }
    }

    @Test
    fun `should not be able do update item quantity when shopping cart status is PAID`() {
        val message = "you cannot update item quantity of a paid shopping cart"
        shoppingCart.status = ShoppingCartStatus.PAID
        every { shoppingCartRepository.findById(any()) } returns Optional.of(shoppingCart)
        assertThatExceptionOfType(BadRequestException::class.java).isThrownBy {
            shoppingCartService.updateItemQuantity(shoppingCart.id!!, item.id!!, 1)
        }.withMessage(message)
        verify(exactly = 1) { shoppingCartRepository.findById(any()) }
        verify(exactly = 0) { productRepository.findById(any()) }
        verify(exactly = 0) { itemRepository.findByShoppingCartIdAndProductId(any(), any()) }
        verify(exactly = 0) { itemRepository.save(any()) }
        verify(exactly = 0) { productRepository.save(any()) }
    }

    @Test
    fun `should not be able to update quantity of a non-existing item`() {
        val message = "item not found"
        every { shoppingCartRepository.findById(any()) } returns Optional.of(shoppingCart)
        every { itemRepository.findById(any()) } throws ItemNotFoundException(message)
        assertThatExceptionOfType(ItemNotFoundException::class.java).isThrownBy {
            shoppingCartService.updateItemQuantity(shoppingCartId = shoppingCart.id!!, itemId = item.id!!, 1)
        }.withMessage(message)
        verify(exactly = 1) { shoppingCartRepository.findById(any()) }
        verify(exactly = 1) { itemRepository.findById(any()) }
        verify(exactly = 0) { productRepository.findById(any()) }
        verify(exactly = 0) { productRepository.save(any()) }
        verify(exactly = 0) { itemRepository.delete(any()) }
    }

    @Test
    fun `should not be able to update quantity of an item when the item does not belong to the shopping cart`() {
        val message = "item not found"
        item.shoppingCart = buildShoppingCart().apply { id = UUID.randomUUID() }
        every { shoppingCartRepository.findById(any()) } returns Optional.of(shoppingCart)
        every { itemRepository.findById(any()) } returns Optional.of(item)
        assertThatExceptionOfType(BadRequestException::class.java).isThrownBy {
            shoppingCartService.updateItemQuantity(shoppingCartId = shoppingCart.id!!, itemId = item.id!!, 1)
        }.withMessage(message)
        verify(exactly = 1) { shoppingCartRepository.findById(any()) }
        verify(exactly = 1) { itemRepository.findById(any()) }
        verify(exactly = 0) { productRepository.findById(any()) }
        verify(exactly = 0) { productRepository.save(any()) }
        verify(exactly = 0) { itemRepository.delete(any()) }
    }

    @Test
    fun `should not be able to update quantity of an item when product quantity in stock is not sufficient`() {
        val message = "the product has not sufficient quantity in stock"
        every { shoppingCartRepository.findById(any()) } returns Optional.of(shoppingCart)
        every { itemRepository.findById(any()) } returns Optional.of(item)
        every { productRepository.findById(any()) } returns Optional.of(product)
        assertThatExceptionOfType(InsufficientStockException::class.java).isThrownBy {
            shoppingCartService.updateItemQuantity(shoppingCartId = shoppingCart.id!!, itemId = item.id!!, 3)
        }.withMessage(message)
        verify(exactly = 1) { shoppingCartRepository.findById(any()) }
        verify(exactly = 1) { itemRepository.findById(any()) }
        verify(exactly = 1) { productRepository.findById(any()) }
        verify(exactly = 0) { productRepository.save(any()) }
        verify(exactly = 0) { itemRepository.delete(any()) }
    }

}